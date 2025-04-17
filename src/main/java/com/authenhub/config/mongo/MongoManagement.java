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
        MongoClientSettings mongoSettingsProperties = getMongoClientSettings();
        return MongoClients.create(mongoSettingsProperties);
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
        return new SimpleMongoClientDatabaseFactory(getMongoClient(),
            mongoConfig.getDatabase());
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
        MongoCredential credential = MongoCredential.createCredential(
            mongoConfig.getUsername(),
            mongoConfig.getAuthentication(),
            mongoConfig.getPassword().toCharArray());

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


        return MongoClientSettings.builder()
            .credential(credential)
            .applyToClusterSettings(builder -> {
                builder.hosts(genAddress());
                if (Objects.nonNull(mongoConfig.getReplicaSet())) {
                    builder.requiredReplicaSetName(mongoConfig.getReplicaSet());
                }
            })
            .applyToConnectionPoolSettings(builder -> builder.applySettings(connectionPoolSettings))
            .applyToServerSettings(builder -> builder.applySettings(serverSettings))
            .build();
    }

    private List<ServerAddress> genAddress() {
        String[] hostPorts = mongoConfig.getServerAddresses().split(",");
        List<ServerAddress> listAddress = new ArrayList<>();
        String[] strHostPort;
        String host;
        String port;

        for (String hostPort : hostPorts) {
            strHostPort = hostPort.split(":");
            host = strHostPort[0];
            port = strHostPort[1];
            final ServerAddress address = new ServerAddress(host, Integer.parseInt(port));
            listAddress.add(address);
        }
        return listAddress;
    }
}
