package com.example.jdog.distributedServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping(path="/message")
    public ResponseEntity<?> postMessage(@RequestBody DistMessage message) {
        logger.info("message from client: {}, {}", message.fromId, message.message);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path="/test")
    public ResponseEntity<?> getTest() {
        logger.info("request received");
        return ResponseEntity.ok().build();
    }

}
