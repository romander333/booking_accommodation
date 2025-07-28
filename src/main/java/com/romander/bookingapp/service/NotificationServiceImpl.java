package com.romander.bookingapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final String token;
    private final String chatId;


    private final RestTemplate restTemplate = new RestTemplate();

    public NotificationServiceImpl(
            @Value("${telegram.token}") String token,
            @Value("${telegram.chatId}") String chatId) {
        this.token = token;
        this.chatId = chatId;
    }

    public void sendMessage(String message) {
        String url = URLDecoder.decode(UriComponentsBuilder.fromHttpUrl(
                "https://api.telegram.org/bot" + token + "/sendMessage")
                .queryParam("chat_id", chatId)
                .queryParam("text", message)
                .toUriString(), StandardCharsets.UTF_8);

        try {
            restTemplate.getForObject(url, String.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
