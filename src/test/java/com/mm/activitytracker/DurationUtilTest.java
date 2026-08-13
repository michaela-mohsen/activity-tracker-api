package com.mm.activitytracker;

import com.mm.activitytracker.entity.postgres.TimeDuration;
import com.mm.activitytracker.util.DurationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class DurationUtilTest {
    @InjectMocks
    private DurationUtil durationUtil;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMillisecondsToTimeDuration() {
        TimeDuration duration = DurationUtil.millisecondsToTimeDuration(BigDecimal.valueOf(1048000));
        Assertions.assertNotEquals(0, duration.getSeconds());
    }
}
