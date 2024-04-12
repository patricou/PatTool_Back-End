package com.pat.service;

import com.pat.controller.ChatGptController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class OpenAIService {

    private static final Logger log = LoggerFactory.getLogger(OpenAIService.class);

    private static final String OPEN_AI_URL = "https://api.openai.com/v1/completions";
    private static final String OPEN_AI_KEY = "xxxxxxxxxxxxx";

    public String getGpt3Response(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        log.info("OpenAI Request : " + prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + OPEN_AI_KEY);

        HttpEntity<String> entity = new HttpEntity<>("{\"prompt\":\"" + prompt + "\", \"max_tokens\":60}", headers);

        ResponseEntity<String> response = restTemplate.exchange(OPEN_AI_URL, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }
}