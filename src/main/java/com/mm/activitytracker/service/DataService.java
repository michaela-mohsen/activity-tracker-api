package com.mm.activitytracker.service;

import com.mm.activitytracker.entity.DataImportRequest;
import com.mm.activitytracker.entity.DataImportResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DataService {
    DataImportResponse importData(MultipartFile file, DataImportRequest request) throws IOException;
}
