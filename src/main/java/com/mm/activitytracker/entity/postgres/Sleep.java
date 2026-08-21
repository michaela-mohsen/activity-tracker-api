package com.mm.activitytracker.entity.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "sleep", indexes = {
        @Index(name = "idx_sleep_user_id", columnList = "user_id")
})
@NoArgsConstructor
@AllArgsConstructor
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

    @Column
    private BigDecimal duration;

    @Column(name =  "user_id")
    private UUID userId;
}
