package com.grh.backend_m1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.grh.backend_m1")
@EnableJpaRepositories(basePackages = "com.grh.backend_m1.repositories")
@EntityScan(basePackages = "com.grh.backend_m1.entities")
public class BackendM1Application {
    public static void main(String[] args) {
        SpringApplication.run(BackendM1Application.class, args);
    }
}