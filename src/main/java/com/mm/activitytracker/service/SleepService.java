package com.mm.activitytracker.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mm.activitytracker.entity.mongodb.Platform;
import com.mm.activitytracker.entity.postgres.Sleep;
import com.mm.activitytracker.model.SleepDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public interface SleepService {
    void save(List<Sleep> sleepList);
    Page<SleepDto> getSleepByUserId(UUID userId, Pageable pageable);
    List<Sleep> getSleepByUserIdAndPlatform(UUID userId, Platform platform);
    void mapToSleep(String dataSection, List<Sleep> sleepList, ObjectNode sleepJson, Map<Long, Sleep> sleepIndex, UUID userId, Platform platform, ZoneId zoneId);
}
