package com.gym.analytics.config;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;

@Configuration
@AllArgsConstructor
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void createIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("trainers");

        Document indexDefinition = new Document();
        indexDefinition.put("firstName", 1);
        indexDefinition.put("lastName", 1);

        indexOps.ensureIndex(new CompoundIndexDefinition(indexDefinition));
    }
}
