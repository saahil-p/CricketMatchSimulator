package org.example.cricketmatchsimulator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class MongoConfigDebug {

    @Value("${spring.data.mongodb.host:localhost}")
    private String host;

    @Value("${spring.data.mongodb.port:27017}")
    private Integer port;

    @Value("${spring.data.mongodb.database:test}")
    private String database;

    @Value("${spring.data.mongodb.uri:#{null}}")
    private String uri;

    @Value("${spring.data.mongodb.auto-index-creation:false}")
    private Boolean autoIndexCreation;

    @EventListener(ApplicationReadyEvent.class)
    public void printMongoConfig() {
        System.out.println("=".repeat(80));
        System.out.println("MongoDB Configuration Properties:");
        System.out.println("=".repeat(80));
        System.out.println("Host: " + host);
        System.out.println("Port: " + port);
        System.out.println("Database: " + database);
        System.out.println("URI: " + uri);
        System.out.println("Auto Index Creation: " + autoIndexCreation);
        System.out.println("=".repeat(80));
    }
}

