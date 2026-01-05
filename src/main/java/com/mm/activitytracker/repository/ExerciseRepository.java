package com.mm.activitytracker.repository;

import com.mm.activitytracker.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, String> {
    Exercise findByOriginalId(long originalId);
}
