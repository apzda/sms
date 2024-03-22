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
package com.apzda.cloud.sms.domain.entity;

import com.apzda.cloud.gsvc.domain.AuditingEntityListener;
import com.apzda.cloud.gsvc.model.Auditable;
import com.apzda.cloud.gsvc.model.SoftDeletable;
import com.apzda.cloud.gsvc.model.Tenantable;
import com.apzda.cloud.sms.domain.SmsStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * @author fengz (windywany@gmail.com)
 * @version 1.0.0
 * @since 1.0.0
 **/
@Entity
@Table(name = "apzda_sms_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class SmsLog implements Auditable<Long, String, Long>, Tenantable<String>, SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long createdAt;

    private String createdBy;

    private Long updatedAt;

    private String updatedBy;

    private String tenantId;

    private boolean deleted;

    private String tid;

    private String phone;

    private String vendor;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SmsStatus status = SmsStatus.PENDING;

    private Long sentTime;

    @Builder.Default
    private int retried = 0;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(name = "intervals")
    private int intervals;

    private String content;

    private String params;

    private String error;

}
