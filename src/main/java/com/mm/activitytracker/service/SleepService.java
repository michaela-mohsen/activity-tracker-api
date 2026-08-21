package com.mm.activitytracker.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mm.activitytracker.entity.postgres.Sleep;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public interface SleepService {
    void save(List<Sleep> sleepList);
    List<Sleep> getSleepByUserId(UUID userId);
    void mapToSleep(String dataSection, List<Sleep> sleepList, ObjectNode sleepJson, Map<Long, Sleep> sleepIndex, UUID userId);
}
