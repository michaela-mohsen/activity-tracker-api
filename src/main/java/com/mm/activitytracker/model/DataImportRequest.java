package com.mm.activitytracker.model;

import com.mm.activitytracker.entity.mongodb.Platform;
import lombok.Data;

import java.util.UUID;

@Data
public class DataImportRequest {
    private Platform sourcePlatform;
    private UUID userId;
}
