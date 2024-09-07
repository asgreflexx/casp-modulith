package casp.web.backend.data.access.layer.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
class MongoConfig {

    private final String connectionString;
    private final String databaseName;
    private final UuidRepresentation uuidRepresentation;

    @Autowired
    MongoConfig(final @Value("${spring.data.mongodb.uri}") String connectionString,
                final @Value("${spring.data.mongodb.database}") String databaseName,
                final @Value("${spring.data.mongodb.uuid-representation}") UuidRepresentation uuidRepresentation) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
        this.uuidRepresentation = uuidRepresentation;
    }

    @Bean
    MongoTransactionManager transactionManager(MongoTemplate mongoTemplate) {
        return new MongoTransactionManager(mongoTemplate.getMongoDatabaseFactory());
    }

    @Bean
    MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, databaseName);
    }

    @Bean
    MongoClient mongoClient() {
        var clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .uuidRepresentation(uuidRepresentation)
                .build();
        return MongoClients.create(clientSettings);
    }
}
