package com.authenhub.config.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class MongoConfig {

    @Value("${mongodb.authentication:}")
    private String authentication;

    @Value("${mongodb.database:authen-hub}")
    private String database;

    @Value("${mongodb.heartbeatFrequency:10000}")
    private int heartBeatFrequency;

    @Value("${mongodb.maxIdleTime:300000}")
    private int maxIdleTime;

    @Value("${mongodb.maxLifeTime:600000}")
    private int maxLifeTime;

    @Value("${mongodb.maxPoolSize:60}")
    private int maxPoolSize;

    @Value("${mongodb.maxWaitTime:600000}")
    private int maxWaitTime;

    @Value("${mongodb.minHeartbeatFrequency:500}")
    private int minHeartBeatFrequency;

    @Value("${mongodb.minPoolSize:10}")
    private int minPoolSize;

    @Value("${mongodb.password:}")
    private String password;

    @Value("${mongodb.replicaSet:}")
    private String replicaSet;

    @Value("${mongodb.serverAddresses:127.0.0.1:27018}")
    private String serverAddresses;

    @Value("${mongodb.username:}")
    private String username;

    @Value("${mongodb.uri:mongodb}")
    private String uri;
}