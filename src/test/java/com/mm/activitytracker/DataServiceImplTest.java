package com.mm.activitytracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mm.activitytracker.entity.CollectedData;
import com.mm.activitytracker.entity.Field;
import com.mm.activitytracker.entity.Platform;
import com.mm.activitytracker.entity.SourcePlatform;
import com.mm.activitytracker.repository.SourcePlatformRepository;
import com.mm.activitytracker.service.ExerciseService;
import com.mm.activitytracker.service.impl.DataServiceImpl;
import com.mm.user.core.entity.User;
import com.mm.user.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class DataServiceImplTest {
    @Mock
    ExerciseService exerciseService;

    @Mock
    SourcePlatformRepository sourcePlatformRepository;

    @Mock
    UserService userService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

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
        Mockito.when(userService.getUserById(mockUserId)).thenReturn(mockUser);
        SourcePlatform fitbitPlatform = mockFitbitPlatform();
        Mockito.when(sourcePlatformRepository.findByPlatform(Platform.FITBIT)).thenReturn(fitbitPlatform);
        Mockito.when(exerciseService.getExercisesByUserId(mockUserId)).thenReturn(new ArrayList<>());
        dataService.importData(mockMultipartFile, Platform.FITBIT, mockUserId);
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

        mockSourcePlatform.setCollectedData(collectedDataList);
        return mockSourcePlatform;
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
