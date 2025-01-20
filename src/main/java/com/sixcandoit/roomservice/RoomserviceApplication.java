package com.sixcandoit.roomservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RoomserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoomserviceApplication.class, args);
    }

}