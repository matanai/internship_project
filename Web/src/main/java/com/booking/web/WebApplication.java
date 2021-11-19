package com.booking.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.booking")
@EnableJpaRepositories(basePackages = { "com.booking.model" })
@EntityScan(basePackages = { "com.booking.model" })
public class WebApplication
{
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
