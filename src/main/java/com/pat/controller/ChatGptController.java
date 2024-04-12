package com.pat.controller;
import com.pat.service.OpenAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatGptController {

    private static final Logger log = LoggerFactory.getLogger(ChatGptController.class);

    private final OpenAIService openAIService;

    @Autowired
    public ChatGptController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @GetMapping("/{prompt}")
    public String getResponse(@PathVariable String prompt) {
        log.info("ChatGpt QUery");

        return openAIService.getGpt3Response(prompt);
    }
}