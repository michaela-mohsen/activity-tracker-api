package com.mm.activitytracker.entity.postgres;

import lombok.Data;

@Data
public class TimeDuration {
    private int hours;
    private int minutes;
    private int seconds;
}
