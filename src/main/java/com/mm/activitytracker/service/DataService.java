package com.mm.activitytracker.service;

import com.mm.activitytracker.model.DataImportResponse;
import com.mm.activitytracker.entity.mongodb.Platform;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface DataService {
    DataImportResponse importData(MultipartFile file, Platform platform, UUID userId, String userTimeZone) throws IOException, MissingServletRequestParameterException;
}
