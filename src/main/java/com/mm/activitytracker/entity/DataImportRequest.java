package com.mm.activitytracker.entity;

import lombok.Data;

import java.io.File;

@Data
public class DataImportRequest {
    private Platform sourcePlatform;
    private String fileType;
    private File file;
}
