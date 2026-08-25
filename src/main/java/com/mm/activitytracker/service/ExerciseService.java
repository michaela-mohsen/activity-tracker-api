package com.mm.activitytracker.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mm.activitytracker.entity.mongodb.Platform;
import com.mm.activitytracker.entity.postgres.Exercise;
import com.mm.activitytracker.model.ExerciseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public interface ExerciseService {
    Page<ExerciseDto> getExercisesByUserId(UUID userId, Pageable pageable);
    List<Exercise> getExercisesByUserId(UUID userId);
    List<Exercise> getExercisesByUserIdAndPlatform(UUID userId, Platform platform);
    void save(List<Exercise> exercises);
    void mapToExercises(String dataSection, List<Exercise> userExercises, ObjectNode exerciseJson, Map<Long, Exercise> exerciseIndex, UUID userId, Platform platform);
}
