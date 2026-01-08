package com.example.userproject.controller;

import com.example.userproject.dto.RegisterRequest;
import com.example.userproject.dto.UserResponse;
import com.example.userproject.models.User;
import com.example.userproject.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService ; //reference to user service

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }




    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        // returns UserResponse obj
        //  @RequestBody RegisterRequest request, take json in the post req and convert it into a RegisterRequest Java object.
        return ResponseEntity.ok(userService.register(request));
    }


    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.existByUserId(userId));
    }




}
