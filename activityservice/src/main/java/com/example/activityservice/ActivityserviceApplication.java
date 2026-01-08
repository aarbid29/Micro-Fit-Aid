package com.example.activityservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ActivityserviceApplication {

	public static void main(String[] args) {
		System.out.println(">>> USING ATLAS CONFIG <<<");
		SpringApplication.run(ActivityserviceApplication.class, args);
	}
}

