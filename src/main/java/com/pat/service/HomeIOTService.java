package com.pat.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HomeIOTService {

    private final RestTemplate restTemplate;

    public HomeIOTService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String openOrClosePortail() {

        String url = "http://192.168.1.65/api/openorclose";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = "{\"command\": \"open\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return  response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            return "Arduino Exception"+ e.getMessage();
        }
    }
}
