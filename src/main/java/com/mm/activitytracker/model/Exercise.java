package com.mm.activitytracker.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;



@Data
@Entity
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column
    private Long originalId;
    @Column
    private OffsetDateTime exerciseStartDate;
    @Column
    private BigDecimal duration;
    @Column
    private String activity;
    @Column
    private DistanceUnit distanceUnit;
    @Column
    private BigDecimal totalCalories;
    @Column
    private BigDecimal totalSteps;
    @Column
    private BigDecimal totalDistance;
    @Column
    private String source;

    //User

}
