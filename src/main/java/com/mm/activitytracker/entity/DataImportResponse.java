package com.mm.activitytracker.entity;

import lombok.Data;

import java.util.List;

@Data
public class DataImportResponse {
    private String id;
    private List<Object> jsonArray;
}
