package com.mm.activitytracker.entity;

import lombok.Data;

import java.util.List;

@Data
public class CollectedData {
    private String dataSection;
    private List<Column> columns;
}
