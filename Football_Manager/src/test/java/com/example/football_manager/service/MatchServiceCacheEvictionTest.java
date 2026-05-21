package com.example.football_manager.service;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.dto.MatchResultRequestDTO;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MatchServiceCacheEvictionTest {

    private static final Logger logger = LoggerFactory.getLogger(MatchServiceCacheEvictionTest.class);

    @Test
    void createMatch_shouldEvictStandingsCache() throws NoSuchMethodException {
        assertMethodEvictsStandingsCache(
                "createMatch",
                MatchRequestDTO.class
        );
    }

    @Test
    void updateMatch_shouldEvictStandingsCache() throws NoSuchMethodException {
        assertMethodEvictsStandingsCache(
                "updateMatch",
                Long.class,
                MatchRequestDTO.class
        );
    }

    @Test
    void deleteMatch_shouldEvictStandingsCache() throws NoSuchMethodException {
        assertMethodEvictsStandingsCache(
                "deleteMatch",
                Long.class
        );
    }

    @Test
    void registerResult_shouldEvictStandingsCache() throws NoSuchMethodException {
        assertMethodEvictsStandingsCache(
                "registerResult",
                Long.class,
                MatchResultRequestDTO.class
        );
    }

    private void assertMethodEvictsStandingsCache(String methodName, Class<?>... parameterTypes)
            throws NoSuchMethodException {

        logger.info("Checking cache eviction annotation: method={}", methodName);
        Method method = MatchService.class.getMethod(methodName, parameterTypes);
        CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);

        assertNotNull(cacheEvict, methodName + " should have @CacheEvict");

        assertTrue(
                Arrays.asList(cacheEvict.cacheNames()).contains("standings"),
                methodName + " should evict the standings cache"
        );

        assertTrue(
                cacheEvict.allEntries(),
                methodName + " should evict all entries from the affected caches"
        );
    }
}