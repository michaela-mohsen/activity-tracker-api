package com.mm.activitytracker.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mm.activitytracker.model.Exercise;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public interface ExerciseService {
    List<Exercise> getExercisesByUserId(UUID userId);
    void save(List<Exercise> exercises);
    void mapToExercises(List<Exercise> userExercises, ObjectNode exerciseJson, Map<Long, Exercise> exerciseIndex, UUID userId);
}
