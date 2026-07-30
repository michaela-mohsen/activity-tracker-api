package com.mm.activitytracker.model;

import com.mm.user.core.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;



@Data
@Entity
@Table(name = "exercise")
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

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
