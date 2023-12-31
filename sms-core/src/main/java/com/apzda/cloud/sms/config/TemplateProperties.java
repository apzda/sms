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
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author fengz (windywany@gmail.com)
 * @version 1.0.0
 * @since 1.0.0
 **/
@Data
public class TemplateProperties {

    /**
     * 短信服务商处的ID
     */
    private String templateId;

    /**
     * 发送间隔
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration interval = Duration.ofMinutes(1);

    /**
     * 有效期
     */
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration timeout = Duration.ofMinutes(30);

    /**
     * 最大重试次数
     */
    private int maxRetries = 0;

    /**
     * 重试间隔
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration retryInterval = Duration.ofSeconds(10);

    /**
     * 短信正文
     */
    private String content;

}
