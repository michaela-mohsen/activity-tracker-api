package com.mm.activitytracker.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mm.activitytracker.model.Exercise;
import com.mm.activitytracker.repository.ExerciseRepository;
import com.mm.activitytracker.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ExerciseServiceImpl implements ExerciseService {
    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
}
