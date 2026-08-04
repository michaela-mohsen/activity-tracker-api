package com.mm.activitytracker.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class DataImportRequest {
    private Platform sourcePlatform;
    private UUID userId;
}
