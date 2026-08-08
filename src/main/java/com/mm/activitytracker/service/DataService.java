package com.mm.activitytracker.service;

import com.mm.activitytracker.entity.DataImportResponse;
import com.mm.activitytracker.entity.Platform;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface DataService {
    DataImportResponse importData(MultipartFile file, Platform platform, UUID userId) throws IOException;
}
