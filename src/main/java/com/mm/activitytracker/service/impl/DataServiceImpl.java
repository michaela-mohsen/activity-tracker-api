package com.mm.activitytracker.service.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mm.activitytracker.entity.*;
import com.mm.activitytracker.model.Exercise;
import com.mm.activitytracker.repository.ExerciseRepository;
import com.mm.activitytracker.repository.SourcePlatformRepository;
import com.mm.activitytracker.service.DataService;
import lombok.extern.slf4j.Slf4j;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
public class DataServiceImpl implements DataService {
    @Autowired
    private SourcePlatformRepository sourcePlatformRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public DataImportResponse importData(MultipartFile file, DataImportRequest request) throws IOException {
        // file can be json, xml, or csv data
        if (request == null) {
            log.error("request not provided");
            throw new RuntimeException("request not provided");
        }
        if (file.isEmpty()) {
            log.error("file not provided");
            throw new RuntimeException("file not provided");
        }
        SourcePlatform sourcePlatform = sourcePlatformRepository.findByPlatform(request.getSourcePlatform());
        if(sourcePlatform == null) {
            log.error("platform not found");
            throw new RuntimeException("platform not found");
        }
        JSONArray jsonArray = new JSONArray();
        objectMapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream(), StandardCharsets.UTF_8)) {
            ZipEntry zipEntry;
            List<String> filesToScan = List.of(".json");
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    String fileName = zipEntry.getName();
                    Optional<CollectedData> collectedDataOptional = sourcePlatform.getCollectedData().stream()
                            .filter(collectedData -> fileName.contains(collectedData.getDataSection())
                                    && filesToScan.stream().anyMatch(fileName::endsWith))
                            .findFirst();
                    if (collectedDataOptional.isPresent()) {
                        log.info("file to search found: {} | {}", collectedDataOptional.get().getDataSection(),
                                fileName);
                        if (fileName.endsWith(".json")) {
                            InputStreamReader reader = new InputStreamReader(zipInputStream, StandardCharsets.UTF_8);
                            CollectedData collectedData = collectedDataOptional.get();
                            Map<String, Column> columnByNameMap = collectedData.getColumns().stream()
                                    .collect(Collectors.toMap(Column::getColumnName, Function.identity()));
                            JsonNode treeNode = objectMapper.readTree(reader);
                            JSONArray innerJsonArray = new JSONArray();
                            treeNode.elements().forEachRemaining(jsonNode -> {
                                JSONObject jsonObject = new JSONObject();
                                columnByNameMap.forEach((name, column) -> {
                                    JsonNode dataNode = getNodeByPath(jsonNode, name);
                                    if (dataNode == null) {
                                        return;
                                    }
                                    String data = dataNode.asText("");
                                    String dataType = column.getDataType();
                                    String formatPattern = column.getFormatPattern();
                                    Object dataToAdd = convert(data, dataType, formatPattern);
                                    jsonObject.put(column.getFieldName(), dataToAdd);
                                });
                                innerJsonArray.put(jsonObject);
                            });
                            // map data to java objects
                            // save data in repository
                            if(collectedData.getDataSection().equals("exercise")) {
                                List<Exercise> mappedExercises = mapJsonArrayToExercise(innerJsonArray);
//                                exerciseRepository.saveAll(mappedExercises);
                            }
                            jsonArray.put(innerJsonArray);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
        }
        DataImportResponse dataImportResponse = new DataImportResponse();
        dataImportResponse.setJsonArray(jsonArray.getJSONArray(21).toList());
        return dataImportResponse;
    }

    private List<Exercise> mapJsonArrayToExercise(JSONArray innerJsonArray) {
        List<Exercise> exerciseList = new ArrayList<>();
        if(!innerJsonArray.isEmpty()) {
            innerJsonArray.forEach(object -> {
                Exercise newExercise = objectMapper.convertValue(object, Exercise.class);
                exerciseList.add(newExercise);
            });
        }
        return exerciseList;
    }

    private JsonNode getNodeByPath(JsonNode jsonNode, String name) {
        JsonNode currentNode = jsonNode;
        for (String part : name.split("\\.")) {
            if (currentNode == null) {
                return null;
            }
            currentNode = currentNode.get(part);
        }
        return currentNode;
    }

    private Object convert(String data, String dataType, String formatPattern) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        return switch (dataType) {
            case "datetime" -> {
                DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(formatPattern, Locale.US);
                LocalDateTime localDateTime = LocalDateTime.parse(data, customFormatter);
                yield localDateTime.atOffset(ZoneOffset.of("Z"));
            }
            case "localdatetime" -> {
                LocalDateTime localDateTime = LocalDateTime.parse(data);
                yield localDateTime.atOffset(ZoneOffset.of("Z"));
            }
            case "timestamp" -> OffsetDateTime.parse(data);
            case "number", "double" -> new BigDecimal(data);
            default -> data.toUpperCase();
        };
    }
}
