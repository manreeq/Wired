package com.group1.wired;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })
@EnableScheduling
public class WiredApplication {

    public static void main(String[] args) {
        SpringApplication.run(WiredApplication.class, args);
        System.out.println("Wired backend is now running!");
    }
}
