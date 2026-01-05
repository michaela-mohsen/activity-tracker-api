package com.mm.activitytracker.entity;

import lombok.Data;

@Data
public class Column {
    private String columnName;
    private String dataType;
    private String fieldName;
    private String formatPattern;
}
