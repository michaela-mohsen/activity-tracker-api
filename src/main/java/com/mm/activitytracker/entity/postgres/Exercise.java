package com.mm.activitytracker.entity.postgres;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "exercises", indexes = {
        @Index(name = "idx_exercises_user_id", columnList = "user_id")
})
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "original_id")
    private Long originalId;

    @Column(name = "exercise_start_date")
    private OffsetDateTime exerciseStartDate;

    @Column
    private BigDecimal duration;

    @Column
    private String activity;

    @Column(name = "distance_unit")
    private DistanceUnit distanceUnit;

    @Column(name = "total_calories")
    private BigDecimal totalCalories;

    @Column(name = "total_steps")
    private BigDecimal totalSteps;

    @Column(name = "total_distance")
    private BigDecimal totalDistance;

    @Column
    private String source;

    @Column(name = "user_id")
    private UUID userId;
}
