package com.danielsanoli.fusionia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FusionIaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FusionIaApiApplication.class, args);
    }
}

