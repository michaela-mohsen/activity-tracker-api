package com.mm.activitytracker.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    public void save(List<Exercise> exercises) {
        exerciseRepository.saveAll(exercises);
    }

    @Override
    public void mapToExercises(List<Exercise> userExercises, ObjectNode exerciseJson, Map<Long, Exercise> exerciseIndex, UUID userId) {
        Exercise newExercise = objectMapper.convertValue(exerciseJson, Exercise.class);
        Exercise existingExercise = exerciseIndex.get(newExercise.getOriginalId());
        if (existingExercise == null) {
            newExercise.setUserId(userId);
            userExercises.add(newExercise);
            exerciseIndex.put(newExercise.getOriginalId(), newExercise);
        }
    }

    private ExerciseDto mapToDto(Exercise exercise) {
        TimeDuration timeDuration = DurationUtil.millisecondsToTimeDuration(exercise.getDuration());
        return ExerciseDto.builder()
                .id(exercise.getId())
                .exerciseStartDate(exercise.getExerciseStartDate().toString())
                .duration(timeDuration)
                .activity(exercise.getActivity())
                .distanceUnit(exercise.getDistanceUnit().toString())
                .totalCalories(exercise.getTotalCalories().intValue())
                .totalSteps(exercise.getTotalSteps().intValue())
                .totalDistance(exercise.getTotalDistance().intValue())
                .source(exercise.getSource())
                .build();
    }
}
