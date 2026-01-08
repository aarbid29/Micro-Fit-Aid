package com.example.userproject.services;

import com.example.userproject.dto.BCryptPasswordEncoder;
import com.example.userproject.dto.RegisterRequest;
import com.example.userproject.dto.UserResponse;
import com.example.userproject.models.User;
//import com.example.userproject.repositories.UserRepository;
import lombok.AllArgsConstructor;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//@AllArgsConstructor
public class UserService {

    private final UserRepository repository;

public UserService(UserRepository repository) {
        this.repository = repository;
    }


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public UserResponse register(RegisterRequest request) {
        if(repository.existsByEmail(request.getEmail())){
            User existingUser = repository.findByEmail(request.getEmail());
            UserResponse response = new UserResponse();
            response.setId(existingUser.getId());
            response.setEmail(existingUser.getEmail());
            response.setFirstName(existingUser.getFirstName());
            response.setLastName(existingUser.getLastName());
            response.setCreatedAt(existingUser.getCreatedAt());
            response.setUpdatedAt(existingUser.getUpdatedAt());


            return response;
        }
        // create new user entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setKeycloakId(request.getKeycloakId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(request.getPassword());


        // hashing password
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);

        // save to db
        User savedUser = repository.save(user);

        //convert to UserResponse DTO
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setKeycloakId(savedUser.getEmail());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());


        return response;
    }

    public UserResponse getUserProfile(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // map User  to UserResponse
        UserResponse response = new UserResponse();
        response.setId(String.valueOf(user.getId()));
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }

    public Boolean existByUserId(String userId) {
        return repository.existsByKeycloakId(userId);

    }
}
