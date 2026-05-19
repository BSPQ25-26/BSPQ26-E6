package com.example.football_manager.service;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.dto.MatchResultRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.cache.annotation.CacheEvict;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MatchServiceCacheEvictionTest {

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