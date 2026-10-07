package com.mm.activitytracker;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mm.activitytracker.entity.mongodb.Platform;
import com.mm.activitytracker.entity.postgres.Sleep;
import com.mm.activitytracker.repository.SleepRepository;
import com.mm.activitytracker.service.impl.SleepServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class SleepServiceImplTest {
    @Mock
    SleepRepository repository;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

    @InjectMocks
    SleepServiceImpl service;

    @Test
    void testMapToSleep() {
        HashMap<Long, Sleep> sleepHashMap = new HashMap<>();
        ZoneId zoneId = ZoneId.systemDefault();
        service.mapToSleep("sleep", new ArrayList<>(), sleepNode(), sleepHashMap, UUID.randomUUID(), Platform.FITBIT, zoneId);
    }

    ObjectNode sleepNode() {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("originalId", 49375205876L);
        node.put("totalMinutesInBed", BigDecimal.valueOf(602));
        node.put("totalMinutesAsleep", BigDecimal.valueOf(486));
        node.put("totalMinutesAwake", BigDecimal.valueOf(116));
        node.put("sleepStartDate", "2025-05-26T00:17:00.000");
        node.put("sleepEndDate", "2025-05-26T10:19:30.000");
        return node;
    }
}
