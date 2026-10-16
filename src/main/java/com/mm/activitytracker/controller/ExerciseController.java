package com.mm.activitytracker.controller;

import com.mm.activitytracker.service.ExerciseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/exercises")
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserExercises(@PathVariable UUID userId, @RequestParam int page, @RequestParam int size, @RequestParam String sortProperty, @RequestParam String direction, @RequestParam(required = false) String activity, @RequestParam(required = false) String dateFrom, @RequestParam(required = false) String dateTo, @RequestParam(required = false) String dateOn) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortProperty));
        log.info("getting user exercises for user id {}, sorting by {} {}. page {}", userId, sortProperty, direction, page);
        return ResponseEntity.ok(exerciseService.getExercisesByUserId(userId, pageable, activity, dateFrom, dateTo, dateOn));
    }
}
