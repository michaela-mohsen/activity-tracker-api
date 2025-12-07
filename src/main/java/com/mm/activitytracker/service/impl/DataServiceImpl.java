package com.mm.activitytracker.service.impl;

import com.mm.activitytracker.entity.*;
import com.mm.activitytracker.repository.SourcePlatformRepository;
import com.mm.activitytracker.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class DataServiceImpl implements DataService {
    @Autowired
    private SourcePlatformRepository sourcePlatformRepository;

    @Override
    public DataImportResponse importData(MultipartFile file, DataImportRequest request) throws IOException {
        // file can be json, xml, or csv data
        if(request == null) {
            log.error("request not provided");
            throw new RuntimeException("request not provided");
        }
        if(file.isEmpty()) {
            log.error("file not provided");
            throw new RuntimeException("file not provided");
        }
        SourcePlatform sourcePlatform = sourcePlatformRepository.findByPlatform(request.getSourcePlatform());
        if(sourcePlatform == null) {
            log.error("platform not found");
            throw new RuntimeException("platform not found");
        }
        JSONArray jsonArray = new JSONArray();
        if(Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            InputStream resourceInputStream = file.getResource().getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceInputStream, StandardCharsets.UTF_8));
            String originalFileName = file.getOriginalFilename().toLowerCase();
            Optional<CollectedData> collectedDataOptional = sourcePlatform.getCollectedData().stream().filter(collectedData1 -> originalFileName.contains(collectedData1.getDataSection())).findFirst();
            if(collectedDataOptional.isEmpty()) {
               log.error("dataSection from file not found");
               throw new RuntimeException("dataSection from file not found");
            }
            CollectedData collectedData = collectedDataOptional.get();
            List<String> headers = new ArrayList<>();
            for (Column column : collectedData.getColumns()) {
                headers.add(column.getColumnName());
            }
            Iterable<CSVRecord> records = CSVFormat.EXCEL.builder().setHeader().get().parse(bufferedReader);
            for(CSVRecord record : records) {
                JSONObject jsonObject = new JSONObject();
                Map<String, String> map = record.toMap();
                for(String header : headers) {
                    Optional<Column> column = collectedData.getColumns().stream().filter(column1 -> column1.getColumnName().equals(header)).findFirst();
                    if(column.isPresent()) {
                        Column currentColumn = column.get();
                        String data = map.get(currentColumn.getColumnName());
                        String dataType = currentColumn.getDataType();
                        Object dataToAdd;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ");
                        if(!StringUtils.isBlank(data)) {
                            dataToAdd = switch (dataType) {
                                case "datetimeoffset" -> {
                                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(data, formatter);
                                    yield zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
                                }
                                case "number" -> BigDecimal.valueOf(Integer.parseInt(data));
                                default -> data;
                            };
                        } else {
                            log.info("no data found for {} in {}", header, collectedData.getDataSection());
                            dataToAdd = null;
                        }
                        jsonObject.put(column.get().getFieldName(), dataToAdd);
                    } else {
                        log.info("column not found for {} header", header);
                    }
                }
                jsonArray.put(jsonObject);
            }
        }
        DataImportResponse dataImportResponse = new DataImportResponse();
        dataImportResponse.setJsonArray(jsonArray.toList());
        return dataImportResponse;
    }
}
