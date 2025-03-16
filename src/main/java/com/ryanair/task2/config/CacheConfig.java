package com.ryanair.task2.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ryanair.task2.domain.model.RouteGraphNode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "cache")
public class CacheConfig {
    private int expirationTime;
    private int maximumSize;

    @Bean
    public Cache<String, Map<String, RouteGraphNode>> ryanairRouteGraphs() {
        return Caffeine.newBuilder()
                .expireAfterWrite(expirationTime, TimeUnit.MINUTES)
                .maximumSize(maximumSize)
                .build();
    }
}
