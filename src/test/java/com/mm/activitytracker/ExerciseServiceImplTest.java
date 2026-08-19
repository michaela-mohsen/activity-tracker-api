package com.mm.activitytracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mm.activitytracker.entity.postgres.DistanceUnit;
import com.mm.activitytracker.entity.postgres.Exercise;
import com.mm.activitytracker.model.ExerciseDto;
import com.mm.activitytracker.repository.ExerciseRepository;
import com.mm.activitytracker.service.impl.ExerciseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceImplTest {

    @Mock
    private ExerciseRepository repository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @InjectMocks
    private ExerciseServiceImpl service;

    @Test
    void testGetExercisesByUserIdPageable() {
        UUID userId = UUID.randomUUID();
        List<Exercise> userExercisesList = existingExercises(userId);
        Page<Exercise> exercises = new PageImpl<>(userExercisesList);
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findByUserId(userId, pageable)).thenReturn(exercises);
        Page<ExerciseDto> page = service.getExercisesByUserId(userId, pageable);
        Assertions.assertEquals(1, page.getTotalPages());
    }

    private List<Exercise> existingExercises(UUID userId) {
        Exercise walkExercise = Exercise.builder()
                .id(UUID.randomUUID())
                .originalId(70058432687L)
                .exerciseStartDate(OffsetDateTime.of(2025, 4, 28, 15, 44, 16, 0, ZoneOffset.of("+03:00")))
                .duration(BigDecimal.valueOf(60))
                .activity("WALK")
                .distanceUnit(DistanceUnit.MILE)
                .totalCalories(BigDecimal.valueOf(78))
                .totalSteps(BigDecimal.valueOf(1202))
                .totalDistance(BigDecimal.valueOf(0.460931))
                .source("CHARGE 6")
                .userId(userId)
                .build();

        Exercise treadmillExercise = Exercise.builder()
                .id(UUID.randomUUID())
                .originalId(70207895609L)
                .exerciseStartDate(OffsetDateTime.of(2025, 5, 5, 14, 38, 27, 0, ZoneOffset.of("+03:00")))
                .duration(BigDecimal.valueOf(938000))
                .activity("TREADMILL")
                .distanceUnit(DistanceUnit.MILE)
                .totalCalories(BigDecimal.valueOf(84))
                .totalSteps(BigDecimal.valueOf(1442))
                .totalDistance(BigDecimal.valueOf(0.602114))
                .source("CHARGE 6")
                .userId(userId)
                .build();
        return List.of(walkExercise, treadmillExercise);
    }
}
