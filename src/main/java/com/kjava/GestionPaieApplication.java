package com.kjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GestionPaieApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionPaieApplication.class, args);
    }
}
