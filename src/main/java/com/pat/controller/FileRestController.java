package com.pat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.pat.domain.Evenement;
import com.pat.domain.FileUploaded;
import com.pat.domain.Member;
import com.pat.repo.EvenementsRepository;
import com.pat.repo.MembersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by patricou on 5/8/2017.
 */
@RestController
public class FileRestController {

    @Value("${app.uploaddir}")
    private String uploadDir;
    @Autowired
    private EvenementsRepository evenementsRepository;
    @Autowired
    private MembersRepository membersRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private MailController mailController;

    private static final Logger log = LoggerFactory.getLogger(FileRestController.class);

    @RequestMapping( value = "/api/file/{fileId}", method = RequestMethod.GET )
    public ResponseEntity< InputStreamResource> getFile(@PathVariable String fileId){

        // retrieve the file in MongoDB
        GridFSDBFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(gridFsFile.getContentType()));
        String filename = gridFsFile.getFilename();
        log.info("Request file " + filename);
        headers.setContentDispositionFormData(filename, filename);
        headers.set("Content-Disposition","inline; filename =" + filename);
        headers.set("Content-Length",Long.toString( gridFsFile.getLength()));

       return ResponseEntity.ok()
                .headers(headers)
                .body( new InputStreamResource(gridFsFile.getInputStream()));
    }

    @PostMapping("/uploadondisk")
    public ResponseEntity<String> handleFileUpload(@RequestParam("files") MultipartFile[] files, HttpServletRequest request) {

        LocalDate date = LocalDate.now();

        // Create a formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        Integer year = date.getYear();

        // Format the LocalDateTime object using the formatter
        String formattedDate = date.format(formatter);

        String dir  = uploadDir + year + File.separator+formattedDate+"_from_uploaded";
        log.info("Dir : " +dir);


        for (MultipartFile file : files) {

                if (!file.isEmpty()) {
                    try {

                        Path uploadPath = Paths.get(dir);

                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }

                        Path filePath = uploadPath.resolve(file.getOriginalFilename());
                        Files.copy(file.getInputStream(), filePath,StandardCopyOption.REPLACE_EXISTING);

                        // log.info("File Uploaded : " + filePath + " Successfully");

                        String ipAddress = request.getHeader("X-Forwarded-For");
                        if (ipAddress == null) {
                            ipAddress = request.getRemoteAddr();
                        }

                        String subject = "Upload Photo on Disk " + filePath.getFileName();
                        String body = subject + "\n" + " from IP : " + ipAddress;
                        body = body + "\n\nHeader : ";

                        Enumeration<String> headerNames = request.getHeaderNames();
                        Map<String, String> headers = new HashMap<>();

                        while (headerNames.hasMoreElements()) {
                            String headerName = headerNames.nextElement();
                            String headerValue = request.getHeader(headerName);

                            ObjectMapper objectMapper = new ObjectMapper();

                            if ("user".equals(headerName.toString())){
                                try{
                                    Member user = objectMapper.readValue(headerValue, Member.class);
                                    subject = subject + " from : "+ user.getUserName() + " ( " + user.getFirstName()+" "+user.getLastName()+" )";
                                }catch(JsonProcessingException je){
                                    log.info("Issue to Unwrap user : " + je.getMessage());
                                }
                            }

                            if (! "authorization".equals(headerName.toString()) )
                                body = body + "\n" + headerName + " : "+ headerValue;
                        }

                        mailController.sendMailWithAttachement(subject,body,filePath.toString());


                    } catch (IOException e) {
                        log.info("File Exception : " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File Upload error : " + e.getMessage());
                    }
                }
            }
        return ResponseEntity.ok("Upload successful");
    }

    @RequestMapping( value = "/uploadfile/{userId}/{evenementid}", method = RequestMethod.POST, consumes = "multipart/form-data")
    // Important note : the name associate with RequestParam is 'file' --> seen in the browser network request.
    public ResponseEntity<FileUploaded> postFile(@RequestParam("file") MultipartFile filedata, @PathVariable String userId, @PathVariable String evenementid  ){
        log.info("Post file received, user.id : " +  userId +" / evenement.id : " + evenementid );

        try {
            Member uploaderMember = membersRepository.findOne(userId);

            DBObject metaData = new BasicDBObject();
            metaData.put("UploaderName", uploaderMember.getFirstName()+" "+uploaderMember.getLastName());
            metaData.put("UploaderId", uploaderMember.getId());

            // Save the doc ( all type ) in  MongoDB
            String fieldId =
                    gridFsTemplate.store( filedata.getInputStream(), filedata.getOriginalFilename(), filedata.getContentType(), metaData).getId().toString();
            log.info("Doc created id : "+fieldId);

            // create the file info
            FileUploaded fileUploaded = new FileUploaded(fieldId, filedata.getOriginalFilename(), filedata.getContentType(), uploaderMember);
            //find the evenement
            Evenement evenement = evenementsRepository.findOne(evenementid);
            evenement.getFileUploadeds().add(fileUploaded);
            // Save the evenement updated
            Evenement eventSaved = evenementsRepository.save(evenement);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(eventSaved.getId()).toUri());

            return new ResponseEntity<FileUploaded>(fileUploaded, httpHeaders, HttpStatus.CREATED);

        }catch (Exception e ){
            log.error(" Exception error " + e);
        }

        return new ResponseEntity<>(null,null,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @RequestMapping( value = "/api/file", method = RequestMethod.PUT )
    public ResponseEntity<Evenement> updateFile(@RequestBody Evenement evenement){

        log.info("Update file for evenement " + evenement.getId());

        // retrieve the evenement id ( with file to delete )
        Evenement evenementNotUpdated = evenementsRepository.findOne(evenement.getId());

        // retrieve the file id to delete
        FileUploaded f = evenementNotUpdated.getFileUploadeds().stream().filter(
                            fileUploaded -> {
                                boolean b = false;
                                for ( FileUploaded fileUploaded2 : evenement.getFileUploadeds())
                                    if ( fileUploaded.getFieldId().equals( fileUploaded2.getFieldId() )) {
                                        b = true;
                                        break;
                                }
                                return !b;
                            }
                        ).findFirst().get();

        log.info("File to delete " + f.getFieldId() );

        // update the evenement without the file ( the save erase all )
        Evenement savedEvenement = evenementsRepository.save(evenement);

        // delete the file in MongoDB
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(f.getFieldId())));

        // return the evenement
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(evenement.getId()).toUri());

        return new ResponseEntity<Evenement>(savedEvenement, httpHeaders, HttpStatus.CREATED);

    }

}
