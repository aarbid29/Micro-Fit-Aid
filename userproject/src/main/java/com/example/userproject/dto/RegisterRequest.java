package com.example.userproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// to define what to take from the user and use in the backend
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    private String keycloakId ;

    @NotBlank(message = "Password is required")
    @Size(min = 6 , message = "Password must have atleast 6 characters")
    private String password;
    private String firstName;
    private String lastName;


    // Getters and Setters
    public String getEmail() {
        return email;
    }


    public String getPassword() {
        return password;
    }
    public String getKeycloakId() {
        return keycloakId;
    }



    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }


    // Optional: toString()
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
