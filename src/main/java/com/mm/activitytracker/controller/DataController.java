package com.mm.activitytracker.controller;

import com.mm.activitytracker.entity.DataImportResponse;
import com.mm.activitytracker.entity.Platform;
import com.mm.activitytracker.service.impl.DataServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/v1/data")
@Slf4j
public class DataController {

    @Autowired
    private DataServiceImpl dataService;

    @PostMapping(path = "/import")
    public ResponseEntity<?> importData(@RequestPart("file") MultipartFile file, @RequestParam Platform platform, @RequestParam UUID userId) throws IOException {
        log.info("file: {}, source platform: {}, user id: {}", file.getName(), platform, userId.toString());
        DataImportResponse dataImportResponse = dataService.importData(file, platform, userId);
        return new ResponseEntity<>(dataImportResponse, HttpStatus.OK);
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportData() {
        return new ResponseEntity<>("exportData successful", HttpStatus.OK);
    }
}
