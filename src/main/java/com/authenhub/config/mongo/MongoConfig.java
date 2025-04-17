package com.authenhub.config.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class MongoConfig {

    @Value("${mongodb.authentication}")
    private String authentication;

    @Value("${mongodb.database}")
    private String database;

    @Value("${mongodb.heartbeatFrequency}")
    private int heartBeatFrequency;

    @Value("${mongodb.maxIdleTime}")
    private int maxIdleTime;

    @Value("${mongodb.maxLifeTime}")
    private int maxLifeTime;

    @Value("${mongodb.maxPoolSize}")
    private int maxPoolSize;

    @Value("${mongodb.maxWaitTime}")
    private int maxWaitTime;

    @Value("${mongodb.minHeartbeatFrequency}")
    private int minHeartBeatFrequency;

    @Value("${mongodb.minPoolSize}")
    private int minPoolSize;

    @Value("${mongodb.password}")
    private String password;

    @Value("${mongodb.replicaSet}")
    private String replicaSet;

    @Value("${mongodb.serverAddresses}")
    private String serverAddresses;

    @Value("${mongodb.username}")
    private String username;
}