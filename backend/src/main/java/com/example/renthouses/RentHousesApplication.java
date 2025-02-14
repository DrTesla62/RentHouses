package com.example.renthouses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.renthouses")
public class RentHousesApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentHousesApplication.class, args);
    }

}
