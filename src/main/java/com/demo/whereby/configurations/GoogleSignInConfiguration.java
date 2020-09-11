package com.demo.whereby.configurations;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class GoogleSignInConfiguration {
    private String clientId;
    private String clientSecret;

    public GoogleSignInConfiguration(@Value("${clientId}") String clientId,
                                     @Value("${clientSecret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Bean
    public GoogleAuthorizationCodeFlow authorizationCodeFlow() {
        HttpTransport transport =  new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        return new GoogleAuthorizationCodeFlow.Builder(transport, jsonFactory, clientId, clientSecret,
                Arrays.asList("profile", "email")).build();
    }
}
