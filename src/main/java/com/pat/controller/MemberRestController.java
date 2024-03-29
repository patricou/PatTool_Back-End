package com.pat.controller;

import com.pat.domain.Member;
import com.pat.repo.MembersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/**
 * Created by patricou on 4/20/2017.
 */
@RestController
@RequestMapping("/api/memb")
public class MemberRestController {

    private static final Logger log = LoggerFactory.getLogger(MemberRestController.class);

    @Autowired
    private MembersRepository membersRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<Member> getListMembers(){
        return membersRepository.findAll();
    }

    @RequestMapping(
            value = "/user",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = { "application/json"}
    )
    @ResponseBody
    public Member getMemberbyUserNameAndRetrieveId(@RequestBody Member member){
        log.info("Member Recieved : " +  member.getKeycloakId() );
        member.setId(null);
        // retrieve Mlab Id by userName ( would have been better by keycloakId )
        Member memberWithId = membersRepository.findByUserName(member.getUserName());
        // Update the ID
        if (memberWithId != null ) {
            log.info("memberWithId " + memberWithId);
            member.setId(memberWithId.getId());
        }

        // Save the member in Mlab ( if modif ( like email or... ) ( userName is unqiue )
        Member newMember = membersRepository.save(member);
        return newMember;
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = { "application/json"}
            )
    public Member getMember(@PathVariable String id) {
        log.info("Get Member : " +  id );
        return membersRepository.findOne(id);
    }

}
