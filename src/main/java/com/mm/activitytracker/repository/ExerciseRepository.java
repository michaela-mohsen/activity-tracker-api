package com.mm.activitytracker.repository;

import com.mm.activitytracker.entity.postgres.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, String> {
    List<Exercise> findByUserId(UUID userId);
    Page<Exercise> findByUserId(UUID userId, Pageable pageable);
    List<Exercise> findByUserIdAndPlatform(UUID userId, String platform);
}
