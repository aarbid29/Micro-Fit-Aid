package com.example.activityservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced
//    WebClient.Builder defines HOW a WebClient will be created, not the WebClient itself.
//    webClientBuilder() is your own Spring @Bean method that returns a WebClient.Builder
//     Spring injects that builder into userServiceWebClient
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }

    //    webClient -> is a real obj here
    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder){
        return webClientBuilder.baseUrl("http://USERPROJECT")
                .build();


    }


}
