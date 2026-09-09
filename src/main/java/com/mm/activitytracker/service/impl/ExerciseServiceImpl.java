package com.mm.activitytracker.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mm.activitytracker.entity.mongodb.Platform;
import com.mm.activitytracker.entity.postgres.Exercise;
import com.mm.activitytracker.entity.postgres.TimeDuration;
import com.mm.activitytracker.model.ExerciseDto;
import com.mm.activitytracker.repository.ExerciseRepository;
import com.mm.activitytracker.service.ExerciseService;
import com.mm.activitytracker.util.DurationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExerciseServiceImpl implements ExerciseService {
    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Page<ExerciseDto> getExercisesByUserId(UUID userId, Pageable pageable) {
        Page<Exercise> userExercises = exerciseRepository.findByUserId(userId, pageable);
        List<ExerciseDto> exerciseDtos = userExercises.getContent().stream().map(this::mapToDto).collect(Collectors.toList());
        return new PageImpl<>(exerciseDtos, pageable, userExercises.getTotalElements());
    }

    @Override
    public List<Exercise> getExercisesByUserId(UUID userId) {
        return exerciseRepository.findByUserId(userId);
    }

    @Override
    public List<Exercise> getExercisesByUserIdAndPlatform(UUID userId, Platform platform) {
        return exerciseRepository.findByUserIdAndPlatform(userId, platform.toString());
    }

    @Override
    public void save(List<Exercise> exercises) {
        exerciseRepository.saveAll(exercises);
    }

    @Override
    public void mapToExercises(String dataSection, List<Exercise> userExercises, ObjectNode exerciseJson, Map<Long, Exercise> exerciseIndex, UUID userId, Platform platform, ZoneId zoneId) {
        if (!dataSection.equals("exercise")) {
            return;
        }
        Exercise newExercise = objectMapper.convertValue(exerciseJson, Exercise.class);
        Exercise existingExercise = exerciseIndex.get(newExercise.getOriginalId());
        if (existingExercise != null) {
            return;
        }
        newExercise.setPlatform(platform.toString());
        newExercise.setUserId(userId);
        newExercise.setZoneId(zoneId.getId());
        userExercises.add(newExercise);
        exerciseIndex.put(newExercise.getOriginalId(), newExercise);
    }

    private ExerciseDto mapToDto(Exercise exercise) {
        TimeDuration timeDuration = DurationUtil.millisecondsToTimeDuration(exercise.getDuration());
        String exerciseStartDate = getExerciseStartDate(exercise);
        return ExerciseDto.builder()
                .id(exercise.getId())
                .exerciseStartDate(exerciseStartDate)
                .duration(timeDuration)
                .activity(exercise.getActivity())
                .distanceUnit(exercise.getDistanceUnit() != null ? exercise.getDistanceUnit().toString() : null)
                .totalCalories(exercise.getTotalCalories() != null ? exercise.getTotalCalories().intValue() : null)
                .totalSteps(exercise.getTotalSteps() != null ? exercise.getTotalSteps().intValue() : null)
                .totalDistance(exercise.getTotalDistance() != null ? exercise.getTotalDistance().intValue() : null)
                .source(exercise.getSource())
                .build();
    }

    private static String getExerciseStartDate(Exercise exercise) {
        String zoneId = exercise.getZoneId();
        ZoneId zone = ZoneId.of(zoneId);
        return exercise.getExerciseStartDate()
                .toInstant()
                .atZone(zone)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
