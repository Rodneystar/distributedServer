package com.example.jdog.distributedServer;

import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class MessageController {

    private final ZkBarrier barrier;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public MessageController( ZkBarrier barrier ) {
        this.barrier = barrier;
    }

    @GetMapping(path="/test")
    public ResponseEntity<?> getTest() {
        try {
            logger.info("request received, waiting at barrier");
            barrier.enter();
            String hostname = InetAddress.getLocalHost().getCanonicalHostName();
            System.out.printf("exitedBarrier: %s\n",  Thread.currentThread().getName());

            System.out.printf("leaving barrier: %s\n", Thread.currentThread().getName());
            barrier.leave();

            System.out.printf("left barrier: %s\n", Thread.currentThread().getName());

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }

}
