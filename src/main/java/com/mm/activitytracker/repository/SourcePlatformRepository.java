package com.mm.activitytracker.repository;

import com.mm.activitytracker.entity.mongodb.Platform;
import com.mm.activitytracker.entity.mongodb.SourcePlatform;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SourcePlatformRepository extends MongoRepository<SourcePlatform, String> {
    SourcePlatform findByPlatform(Platform platform);
}
