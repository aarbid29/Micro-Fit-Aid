package com.example.aiservice.service;

import com.example.aiservice.model.Activity;
import com.example.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class  ActivityMessageListener {
    private final ActivityAIService activityAIService ;
    private final RecommendationInterface recommendationRepository ;

    @KafkaListener(topics= "${spring.kafka.topic.name}",groupId = "activity-processor-group")
    public void processActivity(Activity activity){
        log.info("Received Activity for userId: {}", activity.getUserId());
        try {
            Recommendation recommendation = activityAIService.generateRecommendation(activity);
            recommendationRepository.save(recommendation) ;


        } catch (WebClientResponseException e) {
            log.error("Failed to get recommendation: {}", e.getStatusCode());
        }


        activityAIService.generateRecommendation(activity) ;



    }
}
