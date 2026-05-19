package com.example.football_manager.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(
                new CaffeineCache("matches", Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofSeconds(30))
                        .maximumSize(1000)
                        .build()),
                new CaffeineCache("standings", Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofSeconds(30))
                        .maximumSize(1000)
                        .build()),
                new CaffeineCache("upcomingMatches", Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(1))
                        .maximumSize(1000)
                        .build()),
                new CaffeineCache("competitions", Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(2))
                        .maximumSize(1000)
                        .build()),
                new CaffeineCache("teams", Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(2))
                        .maximumSize(1000)
                        .build()),
                new CaffeineCache("matchResults", Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofSeconds(30))
                        .maximumSize(1000)
                        .build()),
                new CaffeineCache("fantasyLeagues", Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(1))
                        .maximumSize(1000)
                        .build()),
                new CaffeineCache("fantasyLeague", Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(1))
                        .maximumSize(1000)
                        .build()),
                new CaffeineCache("fantasyAvailablePlayers", Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(1))
                        .maximumSize(1000)
                        .build()),
                new CaffeineCache("fantasyLineup", Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(1))
                        .maximumSize(1000)
                        .build()),
                new CaffeineCache("fantasyScore", Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(1))
                        .maximumSize(1000)
                        .build()),
                new CaffeineCache("fantasyLeaderboard", Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(1))
                        .maximumSize(1000)
                        .build())
        ));
        return cacheManager;
    }
}
