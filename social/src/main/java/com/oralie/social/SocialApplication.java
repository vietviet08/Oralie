package com.oralie.social;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SocialApplication {

    public static void main(String[] args) {
        Dotenv.configure().load();
        SpringApplication.run(SocialApplication.class, args);
    }

}
