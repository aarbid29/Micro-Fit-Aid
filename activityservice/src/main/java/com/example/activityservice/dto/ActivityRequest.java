package com.example.activityservice.dto;

import com.example.activityservice.model.ActivityType;
import lombok.Data;
import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;
@Data
public class ActivityRequest {
    private OffsetDateTime startTime;

    private String userId;

    private ActivityType type;

    private Integer duration;

    private Integer caloriesBurned;

//    private LocalDateTime startTime;

    private Map<String, Object> additionalMetrics;

}
