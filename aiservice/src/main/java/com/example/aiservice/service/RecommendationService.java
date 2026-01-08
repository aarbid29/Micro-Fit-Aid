package com.example.aiservice.service;

import com.example.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationInterface repository;

    // to return a list of recommendations for a specific user
    public List<Recommendation> getUserRecommendation(String userId) {
        return repository.findByUserId(userId);
    }

    // to return a single recommendation for a specific activity
    public Recommendation getActivityRecommendation(String activityId) {
        return repository.findByActivityId(activityId);
    }
}

