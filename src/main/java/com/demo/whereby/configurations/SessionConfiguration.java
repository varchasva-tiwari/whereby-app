package com.demo.whereby.configurations;

import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ComponentScan(basePackages = "com.demo.whereby")
public class SessionConfiguration {

    @Bean
    public Map<String, Session> mapSessions(){
        Map<String, Session> mapSessions = new ConcurrentHashMap<>();
        return mapSessions;
    }

    @Bean
    public Map<String, Integer> memberCounter(){
        Map<String, Integer> memberCounter = new ConcurrentHashMap<>();
        return memberCounter;
    }

    @Bean
    public Map<String, Map<String,String>> mapUserToToken(){
        Map<String, Map<String,String>> mapUserToToken = new ConcurrentHashMap<>();
        return mapUserToToken;
    }

    @Bean
    public Map<String , Map<String, OpenViduRole>> mapSessionNamesTokens(){
        Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();
        return mapSessionNamesTokens;
    }

    @Bean
    public OpenVidu getOpenVIduInstance(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
        OpenVidu openVidu = new OpenVidu(openviduUrl, secret);
        return openVidu;
    }

}
