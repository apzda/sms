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
package com.apzda.cloud.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengz (windywany@gmail.com)
 * @version 1.0.0
 * @since 1.0.0
 **/
@ConfigurationProperties(prefix = "apzda.cloud.sms")
@Data
public class SmsConfigProperties {

    /**
     * 短信发送器
     */
    private String sender = "default";

    /**
     * 短信服务商
     */
    private String defaultProvider;

    /**
     * 业务短信模板配置
     */
    private final Map<String, TemplateProperties> templates = new HashMap<>();

    /**
     * 短信服务商配置
     */
    private final List<ProviderProperties> providers = new ArrayList<>();

    /**
     * 发送器配置
     */
    private final Map<String, String> props = new HashMap<>();

}
