package com.gym.analytics.config;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MongoConfigTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private IndexOperations indexOperations;

    @InjectMocks
    private MongoConfig mongoConfig;

    @Test
    void createIndexes_ShouldCreateCompoundIndex() {
        when(mongoTemplate.indexOps("trainers")).thenReturn(indexOperations);

        mongoConfig.createIndexes();

        ArgumentCaptor<CompoundIndexDefinition> captor = ArgumentCaptor.forClass(CompoundIndexDefinition.class);
        verify(indexOperations).ensureIndex(captor.capture());

        CompoundIndexDefinition capturedIndexDefinition = captor.getValue();
        Document indexKeys = capturedIndexDefinition.getIndexKeys();

        assertEquals(1, indexKeys.get("firstName"));
        assertEquals(1, indexKeys.get("lastName"));
    }
}