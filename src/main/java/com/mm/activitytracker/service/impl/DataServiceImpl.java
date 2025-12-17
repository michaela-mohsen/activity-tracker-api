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

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
        try(ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream(), StandardCharsets.UTF_8)) {
            ZipEntry zipEntry;
            List<String> filesToScan = List.of(".csv");
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if(!zipEntry.isDirectory()) {
                    String fileName = zipEntry.getName();
                    Optional<CollectedData> collectedDataOptional = sourcePlatform.getCollectedData().stream().filter(collectedData -> fileName.contains(collectedData.getDataSection()) && filesToScan.stream().anyMatch(fileName::endsWith)).findFirst();
                    if (collectedDataOptional.isPresent()) {
                        log.info("file to search found: {} | {}", collectedDataOptional.get().getDataSection(), fileName);
                        if(fileName.endsWith(".csv")) {
                            Reader reader = new InputStreamReader(zipInputStream, StandardCharsets.UTF_8);
                            CollectedData collectedData = collectedDataOptional.get();
                            List<String> headers = new ArrayList<>();
                            for (Column column : collectedData.getColumns()) {
                                headers.add(column.getColumnName());
                            }
                            Iterable<CSVRecord> records = CSVFormat.EXCEL.builder().setHeader().get().parse(reader);
                            JSONArray innerJsonArray = new JSONArray();
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
                                                case "timestamp" -> OffsetDateTime.parse(data);
                                                case "number" -> BigDecimal.valueOf(Integer.parseInt(data));
                                                case "double" -> BigDecimal.valueOf(Double.parseDouble(data));
                                                default -> data;
                                            };
                                        } else {
                                            dataToAdd = null;
                                        }
                                        jsonObject.put(column.get().getFieldName(), dataToAdd);
                                    } else {
                                        log.info("column not found for {} header", header);
                                    }
                                }
                                innerJsonArray.put(jsonObject);
                            }
                            jsonArray.put(innerJsonArray);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
        }
        // map data to java objects
        // save data in repository
        DataImportResponse dataImportResponse = new DataImportResponse();
        dataImportResponse.setJsonArray(jsonArray.getJSONArray(1).toList());
        return dataImportResponse;
    }
}
