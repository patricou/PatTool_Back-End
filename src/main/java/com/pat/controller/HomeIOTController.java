package com.pat.controller;

import com.pat.domain.Evenement;
import com.pat.service.HomeIOTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HomeIOTController {

    private static final Logger log = LoggerFactory.getLogger(HomeIOTController.class);
    private final HomeIOTService homeIOTService;

    public HomeIOTController(HomeIOTService homeIOTService) {
        this.homeIOTService = homeIOTService;
    }

    @PostMapping(value = "/opcl")
    public String openOrCLosePortail() {
        log.info("Open or close Portail");
        return homeIOTService.openOrClosePortail();
    }

}
