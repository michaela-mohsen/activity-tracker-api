package com.mm.activitytracker.model;

import lombok.Data;

import java.util.List;

@Data
public class DataPage {
    List<ExerciseDto> exercises;
    List<SleepDto> sleeps;
    Long totalElements;
    Integer totalPages;
    Integer currentPage;
    Integer pageSize;
}
