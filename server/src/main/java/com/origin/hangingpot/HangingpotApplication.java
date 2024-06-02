package com.origin.hangingpot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HangingpotApplication {

    public static void main(String[] args) {
        SpringApplication.run(HangingpotApplication.class, args);
    }

}
