package com.pat.controller;

import com.pat.domain.Evenement;
import com.pat.service.SmtpMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MailController {

    private static final Logger log = LoggerFactory.getLogger(MailController.class);

    @Autowired
    private SmtpMailSender smtpMailSender;

    @GetMapping(value = "sendmail/{message}")
    public String sendMail(@PathVariable String message){
        try {
            log.info("Mail to be Sent : " + message);
            //smtpMailSender.sendMail("patrick.paul.pascal.deschamps@gmail.com", "patrick@patrickdeschamps.com", message, message);
            smtpMailSender.sendMail("usermail@patrickdeschamps.com", "patrick@patrickdeschamps.com", message, message);
            log.info("Mail Sent succesfully ");
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    };
}
