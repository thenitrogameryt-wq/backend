package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MongoTestController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @GetMapping("/mongo-test")
    public String test() {

        return "URI: " + uri +
                " | DB: " +
                mongoTemplate.getDb().getName();
    }

    @GetMapping("/mongo-class")
    public String mongoClass() {
        return mongoTemplate.getClass().getName();
    }
}