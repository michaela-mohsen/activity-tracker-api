package com.mm.activitytracker.repository;

import com.mm.activitytracker.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, String> {
    Exercise findByOriginalId(long originalId);
    List<Exercise> findByUserId(UUID userId);
}
