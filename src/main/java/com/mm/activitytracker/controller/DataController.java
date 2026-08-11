package com.mm.activitytracker.controller;

import com.mm.activitytracker.model.DataImportResponse;
import com.mm.activitytracker.entity.mongodb.Platform;
import com.mm.activitytracker.service.impl.DataServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
    public ResponseEntity<?> importData(@RequestPart("file") MultipartFile file, @RequestParam(required = false) Platform platform, @RequestParam(required = false) UUID userId) throws IOException {
        try {
            log.info("file: {}, source platform: {}, user id: {}", file.getName(), platform, userId.toString());
            DataImportResponse dataImportResponse = dataService.importData(file, platform, userId);
            return new ResponseEntity<>(dataImportResponse, HttpStatus.OK);
        } catch (MissingServletRequestParameterException missingParameterException) {
            log.error("Error accepting request: {}", missingParameterException.getMessage());
            return new ResponseEntity<>("Error accepting request: " + missingParameterException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(Exception exception) {
            log.error("Error importing data: {}", exception.getMessage());
            return new ResponseEntity<>("Error importing data: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
