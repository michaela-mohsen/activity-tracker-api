package com.mm.activitytracker.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Field {
    private String dataPath;
    private String dataType;
    private String fieldName;
    private String formatPattern;
}
