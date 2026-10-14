package com.mm.activitytracker.repository;

import com.mm.activitytracker.entity.postgres.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, String> {
    List<Exercise> findByUserId(UUID userId);
    Page<Exercise> findByUserId(UUID userId, Pageable pageable);
    List<Exercise> findByUserIdAndPlatform(UUID userId, String platform);
    Page<Exercise> findByUserIdAndActivityContaining(UUID userId, String activity, Pageable pageable);

    @Query("select e from Exercise e where e.exerciseStartDate <= :exerciseStartDate")
    Page<Exercise> findByUserIdAndExerciseStartDateBefore(@Param("exerciseStartDate") OffsetDateTime exerciseStartDate, Pageable pageable);

    @Query("select e from Exercise e where e.exerciseStartDate >= :exerciseStartDate")
    Page<Exercise> findByUserIdAndExerciseStartDateAfter(@Param("exerciseStartDate") OffsetDateTime exerciseStartDate, Pageable pageable);
}
