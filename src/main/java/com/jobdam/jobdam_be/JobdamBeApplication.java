package com.jobdam.jobdam_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JobdamBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobdamBeApplication.class, args);
    }

}
