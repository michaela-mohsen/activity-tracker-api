package com.mm.activitytracker.repository;

import com.mm.activitytracker.entity.postgres.Sleep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SleepRepository extends JpaRepository<Sleep, Long> {
    List<Sleep> findByUserId(UUID userId);
    List<Sleep> findByUserIdAndPlatform(UUID userId, String platform);
}
