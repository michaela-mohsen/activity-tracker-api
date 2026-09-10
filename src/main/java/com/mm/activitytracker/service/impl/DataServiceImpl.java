package com.mm.activitytracker.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mm.activitytracker.entity.mongodb.CollectedData;
import com.mm.activitytracker.entity.mongodb.Field;
import com.mm.activitytracker.entity.mongodb.Platform;
import com.mm.activitytracker.entity.mongodb.SourcePlatform;
import com.mm.activitytracker.entity.postgres.Sleep;
import com.mm.activitytracker.exception.ResourceNotFoundException;
import com.mm.activitytracker.entity.postgres.Exercise;
import com.mm.activitytracker.model.DataImportResponse;
import com.mm.activitytracker.repository.SourcePlatformRepository;
import com.mm.activitytracker.service.DataService;
import com.mm.activitytracker.service.ExerciseService;
import com.mm.activitytracker.service.SleepService;
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

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
public class DataServiceImpl implements DataService {
    private final SourcePlatformRepository sourcePlatformRepository;
    private final ExerciseService exerciseService;
    private final SleepService sleepService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    private DataServiceImpl(SourcePlatformRepository sourcePlatformRepository, ExerciseService exerciseService, SleepService sleepService, UserService userService, ObjectMapper objectMapper) {
        this.sourcePlatformRepository = sourcePlatformRepository;
        this.exerciseService = exerciseService;
        this.sleepService = sleepService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public DataImportResponse importData(MultipartFile file, Platform platform, UUID userId, String userTimeZone) throws IOException, MissingServletRequestParameterException {
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
        extractAndImport(file, platform, existingUser, sourcePlatform, userTimeZone);
        return new DataImportResponse();
    }

    private void extractAndImport(MultipartFile file, Platform platform, User existingUser, SourcePlatform sourcePlatform, String userTimeZone) throws IOException {
        List<Exercise> userExercises = exerciseService.getExercisesByUserIdAndPlatform(existingUser.getId(), platform);
        Map<Long, Exercise> exerciseIndex = userExercises.stream()
                .collect(Collectors.toMap(Exercise::getOriginalId, Function.identity()));
        List<Sleep> userSleepList = sleepService.getSleepByUserIdAndPlatform(existingUser.getId(), platform);
        Map<Long, Sleep> sleepIndex = userSleepList.stream()
                .collect(Collectors.toMap(Sleep::getOriginalId, Function.identity()));
        ZoneId zoneId = ZoneId.of(userTimeZone);
        Map<String, CollectedData> collectedDataMap = sourcePlatform.getCollectedData().stream().collect(Collectors.toMap(CollectedData::getDataSection, Function.identity()));

        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream(), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            Set<String> filesToScan = Set.of(".json");
            while((entry = zipInputStream.getNextEntry()) != null) {
                Optional<String> fileKey = getFileKey(entry, collectedDataMap, filesToScan);
                if(fileKey.isEmpty()) {
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
                            objectValue = convert(dataFromNode.asText(""), value.getDataType(), value.getFormatPattern(), zoneId);
                            dataObject.putPOJO(value.getFieldName(), objectValue);
                        }
                    });
                    exerciseService.mapToExercises(dataByCategory.getDataSection(), userExercises, dataObject, exerciseIndex, existingUser.getId(), platform, zoneId);
                    sleepService.mapToSleep(dataByCategory.getDataSection(), userSleepList, dataObject, sleepIndex, existingUser.getId(), platform, zoneId);
                });
            }
        } catch (IOException e) {
            log.error("Error reading zip file: {}", e.getMessage());
            throw new IOException("Error reading zip file: " + e.getMessage());
        }
        sleepService.save(userSleepList);
        exerciseService.save(userExercises);
    }

    private Optional<String> getFileKey(ZipEntry entry, Map<String, CollectedData> collectedDataMap, Set<String> filesToScan) {
        if (entry.isDirectory()) {
            return Optional.empty();
        }
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

    private Object convert(String data, String dataType, String formatPattern, ZoneId zoneId) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        return switch (dataType) {
            case "datetime" -> {
                DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(formatPattern, Locale.US);
                ZonedDateTime zonedDateTime = LocalDateTime.parse(data, customFormatter).atZone(UTC);
                yield zonedDateTime.toOffsetDateTime();
            }
            case "localdatetime" -> {
                ZonedDateTime zonedDateTime = LocalDateTime.parse(data).atZone(zoneId);
                yield zonedDateTime.withZoneSameLocal(UTC).toOffsetDateTime();
            }
            case "localdate" -> LocalDate.parse(data);
            case "number", "double" -> new BigDecimal(data);
            default -> data.toUpperCase();
        };
    }
}
