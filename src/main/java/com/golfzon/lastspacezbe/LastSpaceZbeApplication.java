package com.golfzon.lastspacezbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LastSpaceZbeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LastSpaceZbeApplication.class, args);
    }

}