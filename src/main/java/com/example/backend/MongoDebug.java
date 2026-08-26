package com.example.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MongoDebug implements CommandLineRunner {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    public void run(String... args) {
        System.out.println("====================================");
        System.out.println("MongoDB URI configurada: "
                + (mongoUri != null && !mongoUri.isBlank()
                ? "SI"
                : "NO"));

        if (mongoUri != null) {
            System.out.println("Empieza con mongodb: "
                    + mongoUri.startsWith("mongodb"));
            System.out.println("Contiene localhost: "
                    + mongoUri.contains("localhost"));
            System.out.println("Contiene mongodb+srv: "
                    + mongoUri.startsWith("mongodb+srv://"));
        }

        System.out.println("====================================");
    }
}