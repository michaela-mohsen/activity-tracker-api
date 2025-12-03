package com.mm.activitytracker.controller;

import com.mm.activitytracker.entity.DataImportRequest;
import com.mm.activitytracker.entity.DataImportResponse;
import com.mm.activitytracker.service.impl.DataServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/data")
@Slf4j
public class DataController {

    @Autowired
    private DataServiceImpl dataService;

    @PostMapping("/import")
    public ResponseEntity<?> importData(@RequestPart MultipartFile file, @RequestPart DataImportRequest dataImportRequest) throws IOException {
        log.info("file: {}, dataImportRequest: {}", file.getName(), dataImportRequest.toString());
        DataImportResponse dataImportResponse = dataService.importData(file, dataImportRequest);
        return new ResponseEntity<>(dataImportResponse, HttpStatus.OK);
    }

    @GetMapping("export")
    public ResponseEntity<?> exportData() {
        return new ResponseEntity<>("exportData successful", HttpStatus.OK);
    }
}
