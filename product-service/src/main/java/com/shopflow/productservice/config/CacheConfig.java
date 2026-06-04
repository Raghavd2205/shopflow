package com.shopflow.productservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

    // ─── Cache Names ─────────────────────────────
    public static final String PRODUCTS_CACHE = "products";
    public static final String PRODUCT_CACHE = "product";
    public static final String CATEGORIES_CACHE = "categories";
    public static final String CATEGORY_CACHE = "category";

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory
    ) {
        // Default cache config — 5 minutes TTL
        // Create ObjectMapper with LocalDateTime support
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(                                    // ← add this
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(RedisSerializer.string())      // ← add this
                )                                                      // ← add this
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(RedisSerializer.json())
                )
                .disableCachingNullValues();        // Per cache TTL configuration
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        // Products list — 5 minutes
        cacheConfigs.put(PRODUCTS_CACHE,
                defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // Single product — 10 minutes
        cacheConfigs.put(PRODUCT_CACHE,
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // Categories list — 10 minutes
        cacheConfigs.put(CATEGORIES_CACHE,
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // Single category — 10 minutes
        cacheConfigs.put(CATEGORY_CACHE,
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}