package com.pat.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class HomeIOTService {

    private final RestTemplate restTemplate;

    public HomeIOTService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public  Map<String, Object> openOrClosePortail() {

        String url = "http://192.168.1.65/api/openorclose";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = "{\"command\": \"open\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            return  response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            Map map = new HashMap();
            map.put("Arduino Exception", e);
            return map;
        }
    }

    public  Map<String, Object> testEthernetShield2() {

        String url = "http://192.168.1.65/api/test";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = "{\"command\": \"test\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            return  response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            Map map = new HashMap();
            map.put("Arduino Exception during the test", e);
            return map;
        }
    }
}
