package com.authenhub.config.mongo;

import com.authenhub.config.converter.DateToTimestampConverter;
import com.authenhub.config.converter.TimestampToDateConverter;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.ServerSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableMongoAuditing
@RequiredArgsConstructor
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class})
@EnableMongoRepositories(basePackages = "com.authenhub.repository")
public class MongoManagement {

    private final MongoConfig mongoConfig;

    private MongoClient getMongoClient() {
        // Check if we have a MongoDB URI configured
        String mongoUri = mongoConfig.getUri();
        if (mongoUri != null && !mongoUri.isEmpty()) {
            // Use the URI directly if available
            log.info("Connecting to MongoDB using URI: {}", mongoUri.replaceAll(":[^/]+@", ":****@"));
            return MongoClients.create(mongoUri);
        } else {
            // Fall back to using settings
            log.info("Connecting to MongoDB using settings");
            MongoClientSettings mongoSettingsProperties = getMongoClientSettings();
            return MongoClients.create(mongoSettingsProperties);
        }
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new DateToTimestampConverter(),
                new TimestampToDateConverter()
        ));
    }

    @Primary
    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(getMongoDatabaseFactory(), getMappingMongoConverter());
    }

    private MongoDatabaseFactory getMongoDatabaseFactory() {
        String database = mongoConfig.getDatabase();
        if (database == null || database.isEmpty()) {
            // Extract database name from URI if not explicitly set
            String uri = mongoConfig.getUri();
            if (uri != null && !uri.isEmpty()) {
                int lastSlashIndex = uri.lastIndexOf('/');
                if (lastSlashIndex != -1 && lastSlashIndex < uri.length() - 1) {
                    // Extract database name from URI
                    database = uri.substring(lastSlashIndex + 1);
                    // Remove query parameters if any
                    int queryParamIndex = database.indexOf('?');
                    if (queryParamIndex != -1) {
                        database = database.substring(0, queryParamIndex);
                    }
                }
            }

            // If still no database name, use a default
            if (database == null || database.isEmpty()) {
                database = "authen-hub";
            }
        }

        log.info("Using MongoDB database: {}", database);
        return new SimpleMongoClientDatabaseFactory(getMongoClient(), database);
    }

    @Bean
    @Primary
    public MappingMongoConverter getMappingMongoConverter() {
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(getMongoDatabaseFactory()),
            new MongoMappingContext());
        converter.setCustomConversions(mongoCustomConversions());
        converter.afterPropertiesSet();
        return converter;
    }

    private MongoClientSettings getMongoClientSettings() {
        ConnectionPoolSettings connectionPoolSettings = ConnectionPoolSettings.builder()
            .minSize(mongoConfig.getMinPoolSize())
            .maxSize(mongoConfig.getMaxPoolSize())
            .maxConnectionIdleTime(mongoConfig.getMaxIdleTime(), TimeUnit.MILLISECONDS)
            .maxConnectionLifeTime(mongoConfig.getMaxLifeTime(), TimeUnit.MILLISECONDS)
            .maxWaitTime(mongoConfig.getMaxWaitTime(), TimeUnit.MILLISECONDS)
            .build();

        ServerSettings serverSettings = ServerSettings.builder()
            .heartbeatFrequency(mongoConfig.getHeartBeatFrequency(), TimeUnit.MILLISECONDS)
            .minHeartbeatFrequency(mongoConfig.getMinHeartBeatFrequency(), TimeUnit.MILLISECONDS)
            .build();

        MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder()
            .applyToClusterSettings(builder -> {
                builder.hosts(genAddress());
                if (Objects.nonNull(mongoConfig.getReplicaSet()) && !mongoConfig.getReplicaSet().isEmpty()) {
                    builder.requiredReplicaSetName(mongoConfig.getReplicaSet());
                }
            })
            .applyToConnectionPoolSettings(builder -> builder.applySettings(connectionPoolSettings))
            .applyToServerSettings(builder -> builder.applySettings(serverSettings));

        // Add credentials only if username and authentication database are provided
        if (Objects.nonNull(mongoConfig.getUsername()) && !mongoConfig.getUsername().isEmpty()
            && Objects.nonNull(mongoConfig.getAuthentication()) && !mongoConfig.getAuthentication().isEmpty()
            && Objects.nonNull(mongoConfig.getPassword()) && !mongoConfig.getPassword().isEmpty()) {

            MongoCredential credential = MongoCredential.createCredential(
                mongoConfig.getUsername(),
                mongoConfig.getAuthentication(),
                mongoConfig.getPassword().toCharArray());

            settingsBuilder.credential(credential);
        }

        return settingsBuilder.build();
    }

    private List<ServerAddress> genAddress() {
        String serverAddresses = mongoConfig.getServerAddresses();
        if (serverAddresses == null || serverAddresses.isEmpty()) {
            // Default to localhost:27017 if no server addresses are provided
            return List.of(new ServerAddress("localhost", 27017));
        }

        String[] hostPorts = serverAddresses.split(",");
        List<ServerAddress> listAddress = new ArrayList<>();

        for (String hostPort : hostPorts) {
            if (hostPort != null && !hostPort.isEmpty() && hostPort.contains(":")) {
                String[] strHostPort = hostPort.split(":");
                if (strHostPort.length == 2) {
                    String host = strHostPort[0];
                    String port = strHostPort[1];
                    try {
                        final ServerAddress address = new ServerAddress(host, Integer.parseInt(port));
                        listAddress.add(address);
                    } catch (NumberFormatException e) {
                        log.error("Invalid port number in MongoDB server address: {}", hostPort, e);
                    }
                }
            }
        }

        // If no valid addresses were found, default to localhost:27017
        if (listAddress.isEmpty()) {
            return List.of(new ServerAddress("localhost", 27017));
        }

        return listAddress;
    }
}
