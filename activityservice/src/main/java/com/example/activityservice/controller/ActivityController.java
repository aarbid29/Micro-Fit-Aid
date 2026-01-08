package com.example.activityservice.controller;

import com.example.activityservice.dto.ActivityRequest;
import com.example.activityservice.dto.ActivityResponse;
import com.example.activityservice.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

//    @PostMapping
//    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request, @RequestHeader("X-User-ID") String userId) {
//        request.setUserId(userId);
//        return ResponseEntity.ok(activityService.trackActivity(request));
//    }
@PostMapping
public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request,
                                                      @RequestHeader("X-User-ID") String userId) {
    try {
        request.setUserId(userId);
        return ResponseEntity.ok(activityService.trackActivity(request));
    } catch (Exception e) {
        e.printStackTrace(); // prints exact exception
        throw e;
    }
}



    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivities(@RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }
}
