package com.mm.activitytracker.entity;

import com.mm.activitytracker.model.Exercise;
import lombok.Data;

import java.util.List;

@Data
public class DataImportResponse {
    private String id;
    private List<Exercise> jsonArray;
}
