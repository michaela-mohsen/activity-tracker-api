package com.mm.activitytracker.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mm.activitytracker.entity.mongodb.Platform;
import com.mm.activitytracker.entity.postgres.Sleep;
import com.mm.activitytracker.repository.SleepRepository;
import com.mm.activitytracker.service.SleepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class SleepServiceImpl implements SleepService {
    @Autowired
    private SleepRepository sleepRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void save(List<Sleep> sleepList) {
        sleepRepository.saveAll(sleepList);
    }

    @Override
    public List<Sleep> getSleepByUserId(UUID userId) {
        return sleepRepository.findByUserId(userId);
    }

    @Override
    public List<Sleep> getSleepByUserIdAndPlatform(UUID userId, Platform platform) {
        return sleepRepository.findByUserIdAndPlatform(userId, platform.toString());
    }

    @Override
    public void mapToSleep(String dataSection, List<Sleep> sleepList, ObjectNode sleepJson, Map<Long, Sleep> sleepIndex, UUID userId, Platform platform) {
        if (!dataSection.equals("sleep")) {
            return;
        }
        Sleep newSleep = objectMapper.convertValue(sleepJson, Sleep.class);
        Sleep existingSleep = sleepIndex.get(newSleep.getOriginalId());
        if (existingSleep != null) {
            return;
        }
        newSleep.setUserId(userId);
        newSleep.setPlatform(platform.toString());
        sleepList.add(newSleep);
        sleepIndex.put(newSleep.getOriginalId(), newSleep);
    }
}
