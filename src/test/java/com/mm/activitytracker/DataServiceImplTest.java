package com.mm.activitytracker;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mm.activitytracker.entity.mongodb.CollectedData;
import com.mm.activitytracker.entity.mongodb.Field;
import com.mm.activitytracker.entity.mongodb.Platform;
import com.mm.activitytracker.entity.mongodb.SourcePlatform;
import com.mm.activitytracker.repository.SourcePlatformRepository;
import com.mm.activitytracker.service.ExerciseService;
import com.mm.activitytracker.service.SleepService;
import com.mm.activitytracker.service.impl.DataServiceImpl;
import com.mm.user.core.entity.User;
import com.mm.user.core.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataServiceImplTest {
    @Mock
    ExerciseService exerciseService;

    @Mock
    SleepService sleepService;

    @Mock
    SourcePlatformRepository sourcePlatformRepository;

    @Mock
    UserService userService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

    @InjectMocks
    DataServiceImpl dataService;

    ClassPathResource resource;

    UUID mockUserId;

    @BeforeEach
    void setUp() {
        resource = new ClassPathResource("/fitbit-mock-data.zip");
        mockUserId = UUID.randomUUID();
    }

    @Test
    void importData_shouldReturn200() throws IOException, MissingServletRequestParameterException {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fitbit-mock-data.zip", resource.getContentAsByteArray());
        User mockUser = mockUser();
        when(userService.getUserById(mockUserId)).thenReturn(mockUser);
        SourcePlatform fitbitPlatform = mockFitbitPlatform();
        when(sourcePlatformRepository.findByPlatform(Platform.FITBIT)).thenReturn(fitbitPlatform);
        doNothing().when(exerciseService).mapToExercises(anyString(), any(), any(), any(), any(), any(), any());
        doNothing().when(sleepService).mapToSleep(any(), any(), any(), any(), any(), any(), any());
        Assertions.assertNotNull(dataService.importData(mockMultipartFile, Platform.FITBIT, mockUserId, "America/New_York"));
    }

    private User mockUser() {
        User mockUser = new User();
        mockUser.setId(mockUserId);
        return mockUser;
    }

    private SourcePlatform mockFitbitPlatform() {
        SourcePlatform mockSourcePlatform = new SourcePlatform();
        mockSourcePlatform.setPlatform(Platform.FITBIT);

        List<CollectedData> collectedDataList = new ArrayList<>();

        CollectedData exerciseCollectedData = createExerciseCollectedData();
        collectedDataList.add(exerciseCollectedData);

        CollectedData sleepCollectedData = createSleepCollectedData();
        collectedDataList.add(sleepCollectedData);

        mockSourcePlatform.setCollectedData(collectedDataList);
        return mockSourcePlatform;
    }

    private CollectedData createSleepCollectedData() {
        CollectedData sleepCollectedData = new CollectedData();
        sleepCollectedData.setDataSection("sleep");
        List<Field> fields = new ArrayList<>();

        Field originalIdField = new Field("logId", "string", "originalId", null);
        Field totalMinutesInBedField = new Field("timeInBed", "number", "totalMinutesInBed", null);
        Field totalMinutesAsleepField = new Field("minutesAsleep", "number", "totalMinutesAsleep", null);
        Field totalMinutesAwakeField = new Field("minutesAwake", "number", "totalMinutesAwake", null);
        Field sleepStartDateField = new Field("startTime", "localdatetime", "sleepStartDate", null);
        Field sleepEndDateField = new Field("endTime", "localdatetime", "sleepEndDate", null);
        Field durationField = new Field("duration", "number", "duration", null);
        Field sleepDateField = new Field("dateOfSleep", "localdate", "sleepDate", null);

        fields.add(originalIdField);
        fields.add(totalMinutesInBedField);
        fields.add(totalMinutesAsleepField);
        fields.add(totalMinutesAwakeField);
        fields.add(sleepStartDateField);
        fields.add(sleepEndDateField);
        fields.add(durationField);
        fields.add(sleepDateField);

        sleepCollectedData.setFields(fields);
        return sleepCollectedData;
    }

    private CollectedData createExerciseCollectedData() {
        CollectedData exerciseCollectedData = new CollectedData();
        exerciseCollectedData.setDataSection("exercise");
        List<Field> fields = new ArrayList<>();

        Field originalIdField = new Field("logId", "string", "originalId", null);
        Field exerciseStartDateField = new Field("startTime", "datetime", "exerciseStartDate", "MM/dd/yy HH:mm:ss");
        Field durationField = new Field("duration", "number", "duration", null);
        Field activityField = new Field("activityName", "string", "activity", null);
        Field distanceUnitField = new Field("distanceUnit", "string", "distanceUnit", null);
        Field caloriesField = new Field("calories", "number", "totalCalories", null);
        Field totalStepsField = new Field("steps", "number", "totalSteps", null);
        Field totalDistanceField = new Field("distance", "double", "totalDistance", null);
        Field sourceField = new Field("source.name", "string", "source", null);

        fields.add(originalIdField);
        fields.add(exerciseStartDateField);
        fields.add(durationField);
        fields.add(activityField);
        fields.add(distanceUnitField);
        fields.add(caloriesField);
        fields.add(totalStepsField);
        fields.add(totalDistanceField);
        fields.add(sourceField);

        exerciseCollectedData.setFields(fields);
        return exerciseCollectedData;
    }
}
