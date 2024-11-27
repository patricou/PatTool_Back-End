package com.pat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class HomeIOTService {

    private final RestTemplate restTemplate;

    @Value("${app.arduino.ip}")
    private String arduinoIp;

    public HomeIOTService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public  Map<String, Object> openOrClosePortail() {

        String url = "http://"+arduinoIp+"/api/openorclose";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = "{\"command\": \"open\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        return callHttp(url, requestEntity);
    }

    public  Map<String, Object> testEthernetShield2() {

        String url = "http://"+arduinoIp+"/api/test";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = "{\"command\": \"test\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        return callHttp(url, requestEntity);
    }

    private  Map<String, Object> callHttp(String url, HttpEntity<String> requestEntity){
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
            map.put("Arduino Exception during the call to HTTP : ", e);
            return map;
        }
    }
}
