package com.mm.activitytracker.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ExerciseDto {
    private UUID id;
    private String exerciseStartDate;
    private Long duration;
    private String activity;
    private String distanceUnit;
    private Integer totalCalories;
    private Integer totalSteps;
    private Integer totalDistance;
    private String source;
}
