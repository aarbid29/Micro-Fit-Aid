package com.example.userproject.dto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class BCryptPasswordEncoder {

    // encode password
    public String encode(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // simple hash for demo
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash); // return as base64 string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encoding password", e);
        }
    }
}
