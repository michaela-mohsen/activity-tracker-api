package com.mm.activitytracker.service.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mm.activitytracker.entity.mongodb.CollectedData;
import com.mm.activitytracker.entity.mongodb.Field;
import com.mm.activitytracker.entity.mongodb.Platform;
import com.mm.activitytracker.entity.mongodb.SourcePlatform;
import com.mm.activitytracker.exception.ResourceNotFoundException;
import com.mm.activitytracker.entity.postgres.Exercise;
import com.mm.activitytracker.model.DataImportResponse;
import com.mm.activitytracker.repository.SourcePlatformRepository;
import com.mm.activitytracker.service.DataService;
import com.mm.activitytracker.service.ExerciseService;
import com.mm.user.core.entity.User;
import com.mm.user.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
    private final SourcePlatformRepository sourcePlatformRepository;
    private final ExerciseService exerciseService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    private DataServiceImpl(SourcePlatformRepository sourcePlatformRepository, ExerciseService exerciseService, UserService userService, ObjectMapper objectMapper) {
        this.sourcePlatformRepository = sourcePlatformRepository;
        this.exerciseService = exerciseService;
        this.userService = userService;
        this.objectMapper = objectMapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
    }

    @Override
    public DataImportResponse importData(MultipartFile file, Platform platform, UUID userId) throws IOException, MissingServletRequestParameterException {
        if (file.isEmpty()) {
            log.error("File not provided");
            throw new MissingServletRequestParameterException("file", "File not provided in request");
        }
        if (userId == null) {
            log.error("User id not provided");
            throw new MissingServletRequestParameterException("userId", "User id not provided in request");
        }
        User existingUser = userService.getUserById(userId);
        if (existingUser == null) {
            log.error("User not found");
            throw new BadCredentialsException("User not found");
        }
        SourcePlatform sourcePlatform = sourcePlatformRepository.findByPlatform(platform);
        if(sourcePlatform == null) {
            log.error("platform configuration not found");
            throw new ResourceNotFoundException("Platform configuration not found");
        }
        extractAndImport(file, platform, existingUser, sourcePlatform);
        return new DataImportResponse();
    }

    private void extractAndImport(MultipartFile file, Platform platform, User existingUser, SourcePlatform sourcePlatform) throws IOException {
        List<Exercise> userExercises = exerciseService.getExercisesByUserId(existingUser.getId());
        Map<Long, Exercise> exerciseIndex = userExercises.stream()
                .collect(Collectors.toMap(Exercise::getOriginalId, Function.identity()));

        Map<String, CollectedData> collectedDataMap = sourcePlatform.getCollectedData().stream().collect(Collectors.toMap(CollectedData::getDataSection, Function.identity()));

        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream(), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            Set<String> filesToScan = Set.of(".json");
            while((entry = zipInputStream.getNextEntry()) != null) {
                Optional<String> fileKey = getFileKey(entry, collectedDataMap, filesToScan);
                if(entry.isDirectory() || fileKey.isEmpty()) {
                    zipInputStream.closeEntry();
                    continue;
                }
                CollectedData dataByCategory = collectedDataMap.get(fileKey.get());
                log.info("{} data section found in {}: {}", platform, entry.getName(), dataByCategory.getDataSection());
                Map<String, Field> dataPathMap = dataByCategory.getFields().stream().collect(Collectors.toMap(Field::getDataPath, Function.identity()));
                InputStreamReader entryReader = new InputStreamReader(zipInputStream, StandardCharsets.UTF_8);
                JsonNode treeNode = objectMapper.readTree(entryReader);
                treeNode.elements().forEachRemaining(jsonNodeFromTree -> {
                    ObjectNode dataObject = objectMapper.createObjectNode();
                    dataPathMap.forEach((key, value) -> {
                        JsonNode dataFromNode = getNodeByPath(jsonNodeFromTree, value.getDataPath());
                        Object objectValue;
                        if (dataFromNode != null) {
                            objectValue = convert(dataFromNode.asText(""), value.getDataType(), value.getFormatPattern());
                            dataObject.putPOJO(value.getFieldName(), objectValue);
                        }
                    });
                    exerciseService.mapToExercises(dataByCategory.getDataSection(), userExercises, dataObject, exerciseIndex, existingUser.getId());
                });
            }
        } catch (IOException e) {
            log.error("Error reading zip file: {}", e.getMessage());
            throw new IOException("Error reading zip file: " + e.getMessage());
        }
        exerciseService.save(userExercises);
    }

    private Optional<String> getFileKey(ZipEntry entry, Map<String, CollectedData> collectedDataMap, Set<String> filesToScan) {
        String fileName = entry.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        Optional<String> fileKey = collectedDataMap.keySet().stream().filter(fileName::contains).findFirst();
        if (!filesToScan.contains(fileExtension) || fileKey.isEmpty()) {
            return Optional.empty();
        }
        return fileKey;
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
