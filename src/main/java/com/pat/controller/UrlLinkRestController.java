package com.pat.controller;

import com.pat.domain.UrlLink;
import com.pat.repo.UrlLinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/urllink")
public class UrlLinkRestController {

    private static final Logger log = LoggerFactory.getLogger(UrlLinkRestController.class);

    @Autowired
    UrlLinkRepository urlLinkRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<UrlLink> getUrlLink(){

        log.info("Get urlLink");
        Sort sort = new Sort(Sort.Direction.ASC,"linkName");
        return urlLinkRepository.findAll(sort);
    }


}
