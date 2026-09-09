package com.mm.activitytracker.controller;

import com.mm.activitytracker.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/exercises")
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserExercises(@PathVariable UUID userId, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("exerciseStartDate").descending());
        return ResponseEntity.ok(exerciseService.getExercisesByUserId(userId, pageable));
    }
}
