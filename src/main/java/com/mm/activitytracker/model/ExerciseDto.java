package com.mm.activitytracker.model;

import com.mm.activitytracker.entity.postgres.TimeDuration;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ExerciseDto {
    private UUID id;
    private String exerciseStartDate;
    private TimeDuration duration;
    private String activity;
    private String distanceUnit;
    private Integer totalCalories;
    private Integer totalSteps;
    private Integer totalDistance;
    private String source;
}
