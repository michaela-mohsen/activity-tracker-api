package com.mm.activitytracker.controller;

import com.mm.activitytracker.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/exercises")
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;

    @GetMapping()
    public ResponseEntity<?> getExercises(@RequestParam UUID userId) {
        return ResponseEntity.ok(exerciseService.getExercisesByUserId(userId));
    }
}
