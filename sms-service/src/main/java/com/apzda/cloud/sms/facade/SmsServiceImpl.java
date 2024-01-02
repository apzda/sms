/*
 * Copyright (C) 2023-2023 Fengz Ning (windywany@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.apzda.cloud.sms.facade;

import com.apzda.cloud.gsvc.ext.GsvcExt;
import com.apzda.cloud.gsvc.infra.Counter;
import com.apzda.cloud.gsvc.infra.TempStorage;
import com.apzda.cloud.gsvc.utils.I18nHelper;
import com.apzda.cloud.sms.SmsProvider;
import com.apzda.cloud.sms.SmsSender;
import com.apzda.cloud.sms.SmsTemplate;
import com.apzda.cloud.sms.config.SmsServiceConfig;
import com.apzda.cloud.sms.config.TemplateProperties;
import com.apzda.cloud.sms.domain.entity.SmsLog;
import com.apzda.cloud.sms.domain.repository.SmsLogRepository;
import com.apzda.cloud.sms.dto.Variable;
import com.apzda.cloud.sms.exception.TooManySmsException;
import com.apzda.cloud.sms.proto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @author fengz (windywany@gmail.com)
 * @version 1.0.0
 * @since 1.0.0
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService, InitializingBean {

    private final SmsLogRepository smsLogRepository;

    private final SmsServiceConfig serviceConfig;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final TempStorage storage;

    private final Counter counter;

    private SmsSender smsSender;

    private String vendor;

    @Override
    public void afterPropertiesSet() throws Exception {
        smsSender = serviceConfig.getSmsSender();
        SmsProvider smsProvider = serviceConfig.getSmsProvider();
        vendor = smsProvider.getId();
    }

    @Override
    public SendRes send(SendReq request) {
        val builder = SendRes.newBuilder();
        val phones = request.getPhoneList();
        val variables = request.getVariableList();
        val templateId = request.getTid();
        var sync = request.getSync();

        if (request.getPhoneCount() > 1) {
            sync = false;
        }

        val template = getSmsTemplate(templateId);
        if (template == null) {
            log.warn("Sms Template({}) is not available", templateId);
            builder.setErrCode(1);
            builder.setErrMsg(I18nHelper.t("sms.template.not.found", new String[] { templateId }));
            return builder.build();
        }

        val params = variables.stream().map(variable -> new Variable(variable.getName(), variable.getValue())).toList();

        for (String phone : phones) {
            try {
                checkLimit(phone, templateId, template.getProperties());
                val sms = template.create(phone, params);
                sms.setSync(sync);
                sms.setVendor(vendor);
                template.beforeSend(sms, storage);
                val smsLog = new SmsLog();
                smsLog.setPhone(sms.getPhone());
                smsLog.setTid(sms.getTid());
                smsLog.setVendor(vendor);
                smsLog.setIntervals(sms.getIntervals());
                smsLog.setContent(sms.getContent());
                smsLog.setParams(Variable.toJsonStr(params));
                smsLogRepository.save(smsLog);
                sms.setSmsLogId(smsLog.getId());
                smsSender.send(sms, applicationEventPublisher);
                template.onSent(sms, storage);
            }
            catch (Exception e) {
                log.warn("Cannot send sms({},{},{}) - {}", phone, templateId, params, e.getMessage());
                builder.setErrCode(500);
                builder.setErrMsg(I18nHelper.t("sms.send.failed"));
                return builder.build();
            }
        }

        builder.setErrCode(0);
        builder.setInterval((int) template.getProperties().getInterval().toSeconds());
        return builder.build();
    }

    @Override
    public GsvcExt.CommonRes verify(VerifyReq request) {
        val builder = GsvcExt.CommonRes.newBuilder();
        val templateId = request.getTid();
        val phone = request.getPhone();
        val variables = request.getVariableList();

        val template = getSmsTemplate(templateId);
        if (template == null) {
            log.warn("Sms Template({}) is not available", templateId);
            builder.setErrCode(1);
            builder.setErrMsg(I18nHelper.t("sms.template.not.found", new String[] { templateId }));
            return builder.build();
        }

        val params = variables.stream().map(variable -> new Variable(variable.getName(), variable.getValue())).toList();

        val sms = template.create(phone, params);
        if (template.verify(sms, storage)) {
            builder.setErrCode(0);
        }
        else {
            builder.setErrCode(1);
            builder.setErrMsg(I18nHelper.t("sms.invalid"));
        }
        return builder.build();
    }

    @Override
    public QueryRes logs(Query request) {
        return null;
    }

    private void checkLimit(String phone, String tid, TemplateProperties properties) {
        val ac = counter.count("sms.ld." + phone, Duration.ofDays(1).toSeconds());
        val pac = serviceConfig.getProperties().getMaxCount();
        if (ac > pac) {
            throw new TooManySmsException();
        }

        val dc = counter.count("sms.ld." + tid + "." + phone, Duration.ofDays(1).toSeconds());
        val pdc = properties.getCountD();
        if (dc > pdc) {
            throw new TooManySmsException();
        }
        val hc = counter.count("sms.lh." + tid + "." + phone, Duration.ofHours(1).toSeconds());
        val phc = properties.getCountH();
        if (hc > phc) {
            throw new TooManySmsException();
        }
        val mc = counter.count("sms.lm." + tid + "." + phone, 60);
        val pmc = properties.getCountH();
        if (mc > pmc) {
            throw new TooManySmsException();
        }
    }

    private SmsTemplate getSmsTemplate(String templateId) {
        val properties = serviceConfig.getProperties();
        val templates = properties.getTemplates();
        val providerProperties = serviceConfig.getProviderProperties();
        val pp = providerProperties.get(vendor);
        val tpl = pp.templates(templates);
        return tpl.get(templateId);
    }

}
