package com.mm.activitytracker.entity.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "sleep", indexes = {
        @Index(name = "idx_sleep_user_id", columnList = "user_id"),
        @Index(name = "idx_sleep_platform_user_id", columnList = "platform, user_id")
})
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Sleep {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "original_id")
    private Long originalId;

    @Column(name = "total_minutes_in_bed")
    private BigDecimal totalMinutesInBed;

    @Column(name =  "total_minutes_asleep")
    private BigDecimal totalMinutesAsleep;

    @Column(name = "total_minutes_awake")
    private BigDecimal totalMinutesAwake;

    @Column(name = "sleep_start_date")
    private OffsetDateTime sleepStartDate;

    @Column(name = "sleep_end_date")
    private OffsetDateTime sleepEndDate;

    @Column(name = "zone_id")
    private String zoneId;

    @Column
    private BigDecimal duration;

    @Column(name =  "user_id")
    private UUID userId;

    @Column(name="sleep_date")
    private LocalDate sleepDate;

    @Column
    private String platform;

    @CreatedDate
    @Column(name =  "created_date", updatable = false, nullable = false)
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    private Instant lastModifiedDate;
}
