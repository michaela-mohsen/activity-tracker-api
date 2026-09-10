package com.mm.activitytracker.model;

import com.mm.activitytracker.entity.postgres.TimeDuration;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SleepDto {
    private UUID uuid;
    private SleepMinutes sleepMinutes;
    private String sleepStartDate;
    private String sleepEndDate;
    private TimeDuration duration;
    private String sleepDate;
    private String platform;
}
