package com.mm.activitytracker.model;

import com.mm.activitytracker.entity.postgres.Exercise;
import lombok.Data;

import java.util.List;

@Data
public class DataImportResponse {
    private String id;
    private List<Exercise> jsonArray;
}
