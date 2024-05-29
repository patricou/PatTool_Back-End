package com.pat.service;

import com.pat.domain.ChatRequest;
import com.pat.repo.ChatRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    @Value("${openai.key}")
    private String apiKey;

    @Value("${openai.api}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ChatRequestRepository chatRequestRepository;

    public ChatService(RestTemplate restTemplate, ChatRequestRepository chatRequestRepository) {
        this.restTemplate = restTemplate;
        this.chatRequestRepository = chatRequestRepository;
    }

    public  String getChatResponse(String userInput, boolean withHistoricalContext) {
        // Récupérer tout l'historique des requêtes
        List<ChatRequest> chatHistory = chatRequestRepository.findAll();

        // Construire le contexte de la conversation ( if without context it is less expensive )
        String context = withHistoricalContext ?
                //true
                chatHistory.stream()
                .map(chatRequest -> "User: " + chatRequest.getUserInput() + "\nAI: " + chatRequest.getChatResponse())
                .collect(Collectors.joining("\n")) + "\nUser: " + userInput
                :
                // false
                "\nUser: " + userInput;

        //log.info("Context : {} ",context);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        //headers.set("Content-Type", "application/json; charset=UTF-8");
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construire le corps de la requête en utilisant une Map
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", List.of(Map.of("role", "user", "content", context)));
        requestBody.put("max_tokens", 1000);
        requestBody.put("model", "gpt-4o");  // Use the correct model you have access to

        //log.info("Request Body :  " + requestBody);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        // Enregistrer la nouvelle requête et la réponse dans la base de données
        ChatRequest chatRequest = new ChatRequest(userInput, response.getBody());

        chatRequestRepository.save(chatRequest);

        return  decodeUtf8(response.getBody());
    }

    private String decodeUtf8(String input) {
        byte[] bytes = input.getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void deletePatGptHistorical(){
        chatRequestRepository.deleteAll();
    }
}