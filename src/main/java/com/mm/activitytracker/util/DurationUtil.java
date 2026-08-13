package com.mm.activitytracker.util;

import com.mm.activitytracker.entity.postgres.TimeDuration;

import java.math.BigDecimal;

public class DurationUtil {
    public static TimeDuration millisecondsToTimeDuration(BigDecimal milliseconds) {
        TimeDuration timeDuration = new TimeDuration();
        if(milliseconds == null) {
            return timeDuration;
        }
        int millisecondsInt = milliseconds.intValue();
        int seconds = millisecondsInt / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        timeDuration.setHours(hours);
        timeDuration.setMinutes(minutes % 60);
        timeDuration.setSeconds(seconds % 60);
        return timeDuration;
    }
}
