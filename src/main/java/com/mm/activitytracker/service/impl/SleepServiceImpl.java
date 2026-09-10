package com.mm.activitytracker.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mm.activitytracker.entity.mongodb.Platform;
import com.mm.activitytracker.entity.postgres.Sleep;
import com.mm.activitytracker.entity.postgres.TimeDuration;
import com.mm.activitytracker.model.SleepDto;
import com.mm.activitytracker.model.SleepMinutes;
import com.mm.activitytracker.repository.SleepRepository;
import com.mm.activitytracker.service.SleepService;
import com.mm.activitytracker.util.DurationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    public Page<SleepDto> getSleepByUserId(UUID userId, Pageable pageable) {
        Page<Sleep> sleepPage = sleepRepository.findByUserId(userId, pageable);
        List<SleepDto> sleepDtos = sleepPage.getContent().stream().map(this::mapToDto).toList();
        return new PageImpl<>(sleepDtos, pageable, sleepPage.getTotalElements());
    }

    @Override
    public List<Sleep> getSleepByUserIdAndPlatform(UUID userId, Platform platform) {
        return sleepRepository.findByUserIdAndPlatform(userId, platform.toString());
    }

    @Override
    public void mapToSleep(String dataSection, List<Sleep> sleepList, ObjectNode sleepJson, Map<Long, Sleep> sleepIndex, UUID userId, Platform platform, ZoneId zoneId) {
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
        newSleep.setZoneId(zoneId.getId());
        sleepList.add(newSleep);
        sleepIndex.put(newSleep.getOriginalId(), newSleep);
    }

    private SleepDto mapToDto(Sleep sleep) {
        SleepMinutes sleepMinutes = createSleepMinutes(sleep);
        String sleepStartDate = getFormattedZoneDateTime(sleep.getZoneId(), sleep.getSleepStartDate());
        String sleepEndDate = getFormattedZoneDateTime(sleep.getZoneId(), sleep.getSleepEndDate());
        TimeDuration duration = DurationUtil.millisecondsToTimeDuration(sleep.getDuration());
        return SleepDto.builder()
                .uuid(sleep.getId())
                .sleepMinutes(sleepMinutes)
                .sleepStartDate(sleepStartDate)
                .sleepEndDate(sleepEndDate)
                .duration(duration)
                .sleepDate(sleep.getSleepDate().toString())
                .platform(sleep.getPlatform())
                .build();
    }

    private static SleepMinutes createSleepMinutes(Sleep sleep) {
        SleepMinutes sleepMinutes = new SleepMinutes();
        sleepMinutes.setInBedMinutes(sleep.getTotalMinutesInBed() != null ? sleep.getTotalMinutesInBed().intValue() : null);
        sleepMinutes.setAsleepMinutes(sleep.getTotalMinutesAsleep() != null ? sleep.getTotalMinutesAsleep().intValue() : null);
        sleepMinutes.setAwakeMinutes(sleep.getTotalMinutesAwake() != null ? sleep.getTotalMinutesAwake().intValue() :  null);
        return sleepMinutes;
    }

    private static String getFormattedZoneDateTime(String zoneId, OffsetDateTime offsetDateTime) {
        ZoneId zone = ZoneId.of(zoneId);
        return offsetDateTime
                .toInstant()
                .atZone(zone)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
