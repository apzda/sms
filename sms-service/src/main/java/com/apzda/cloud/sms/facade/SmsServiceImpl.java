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
import com.apzda.cloud.sms.SmsSender;
import com.apzda.cloud.sms.config.SmsServiceConfig;
import com.apzda.cloud.sms.domain.entity.SmsLog;
import com.apzda.cloud.sms.domain.repository.SmsLogRepository;
import com.apzda.cloud.sms.dto.Sms;
import com.apzda.cloud.sms.proto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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

    private SmsSender smsSender;

    @Override
    public void afterPropertiesSet() throws Exception {
        smsSender = serviceConfig.getSmsSender();
    }

    @Override
    public SendRes send(SendReq request) {
        val smsLog = new SmsLog();
        smsLog.setPhone("18049920019");
        smsLog.setVendor("dayu");
        smsLog.setTid("login");
        smsLog.setIntervals(60);
        smsLog.setContent("test");

        log.error("sms log = {}", smsLog);
        smsLogRepository.save(smsLog);
        val sms = new Sms();
        sms.setSmsLogId(smsLog.getId());
        sms.setTemplateId(smsLog.getTenantId());
        sms.setVendor(smsLog.getVendor());

        try {
            smsSender.send("18049920019", sms, applicationEventPublisher);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        val builder = SendRes.newBuilder();
        builder.setErrCode(0);
        builder.setInterval(60);
        return builder.build();
    }

    @Override
    public GsvcExt.CommonRes verify(VerifyReq request) {
        return null;
    }

    @Override
    public QueryRes logs(Query request) {
        return null;
    }

}
