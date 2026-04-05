package com.sureprompt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SurePromptApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurePromptApplication.class, args);
        System.out.println("👉 http://localhost:8080");
    }
}
