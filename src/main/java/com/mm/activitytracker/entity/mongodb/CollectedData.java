package com.mm.activitytracker.entity.mongodb;

import lombok.Data;

import java.util.List;

@Data
public class CollectedData {
    private String dataSection;
    private List<Field> fields;
}
