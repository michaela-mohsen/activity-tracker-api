package com.mm.activitytracker.model;

import lombok.Data;

@Data
public class SleepMinutes {
    private Integer inBedMinutes;
    private Integer asleepMinutes;
    private Integer awakeMinutes;
}
