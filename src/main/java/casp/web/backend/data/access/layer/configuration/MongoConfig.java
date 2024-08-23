package casp.web.backend.data.access.layer.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
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

    @Autowired
    MongoConfig(final @Value("${spring.data.mongodb.uri}") String connectionString, final @Value("${spring.data.mongodb.database}") String databaseName) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
    }

    @Bean
    MongoTransactionManager transactionManager(MongoClient mongoClient) {
        return new MongoTransactionManager(mongoTemplate(mongoClient).getMongoDatabaseFactory());
    }

    @Bean
    MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, databaseName);
    }

    @Bean
    MongoClient mongoClient() {
        return MongoClients.create(connectionString);
    }
}
