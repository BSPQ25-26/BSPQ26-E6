package com.example.football_manager.performance;

import com.example.football_manager.model.Country;
import com.example.football_manager.model.Team;
import com.example.football_manager.repository.CountryRepository;
import com.example.football_manager.repository.TeamRepository;
import com.example.football_manager.service.TeamService;
import com.github.noconnor.junitperf.JUnitPerfRule;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;

/**
 * Performance tests for TeamService using JUnitPerf.
 *
 * <p>These tests validate repeated and concurrent execution of the
 * getAllTeams operation. JUnitPerf is used as a replacement for the
 * deprecated ContiPerf library.</p>
 *
 * <p>The tests use lightweight repository proxies instead of a real database
 * in order to focus on service execution and avoid external dependencies.</p>
 */
public class TeamServicePerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(TeamServicePerformanceTest.class);

    /**
     * JUnitPerf rule required to execute performance tests.
     */
    @Rule
    public JUnitPerfRule perfTestRule = new JUnitPerfRule();

    private TeamService teamService;

    /**
     * Prepares test data and fake repositories before each performance test.
     */
    @Before
    public void setUp() {
        logger.info("Preparing TeamService performance test data");

        List<Team> sampleTeams = createSampleTeams(100);

        TeamRepository teamRepository = createRepositoryProxy(
                TeamRepository.class,
                sampleTeams
        );

        CountryRepository countryRepository = createRepositoryProxy(
                CountryRepository.class,
                List.of()
        );

        teamService = new TeamService(teamRepository, countryRepository);

        logger.info("TeamService performance test setup completed with {} sample teams", sampleTeams.size());
    }

    /**
     * Validates TeamService performance using a single execution thread.
     */
    @Test
    @JUnitPerfTest(
            threads = 1,
            durationMs = 3000,
            warmUpMs = 500
    )
    @JUnitPerfTestRequirement(
            meanLatency = 15,
            maxLatency = 100,
            executionsPerSec = 50
    )
    public void getAllTeams_singleThreadPerformance_shouldPass() {
        logger.info("Running TeamService performance test: single thread");

        List<Team> teams = teamService.getAllTeams();

        logger.info("Single thread performance test returned {} teams", teams.size());

        assertFalse(teams.isEmpty());
    }

    /**
     * Validates TeamService performance under multiple concurrent threads.
     */
    @Test
    @JUnitPerfTest(
            threads = 5,
            durationMs = 3000,
            warmUpMs = 500
    )
    @JUnitPerfTestRequirement(
            meanLatency = 25,
            maxLatency = 150,
            executionsPerSec = 150
    )
    public void getAllTeams_multipleThreadsPerformance_shouldPass() {
        logger.info("Running TeamService performance test: multiple threads");

        List<Team> teams = teamService.getAllTeams();

        logger.info("Multiple threads performance test returned {} teams", teams.size());

        assertFalse(teams.isEmpty());
    }

    /**
     * Validates that TeamService reaches the expected throughput.
     */
    @Test
    @JUnitPerfTest(
            threads = 10,
            durationMs = 4000,
            warmUpMs = 500
    )
    @JUnitPerfTestRequirement(
            meanLatency = 30,
            maxLatency = 200,
            executionsPerSec = 300
    )
    public void getAllTeams_throughputPerformance_shouldPass() {
        logger.info("Running TeamService performance test: throughput");

        List<Team> teams = teamService.getAllTeams();

        logger.info("Throughput performance test returned {} teams", teams.size());

        assertFalse(teams.isEmpty());
    }

    /**
     * Validates TeamService stability during a longer performance test duration.
     */
    @Test
    @JUnitPerfTest(
            threads = 3,
            durationMs = 7000,
            warmUpMs = 500
    )
    @JUnitPerfTestRequirement(
            meanLatency = 25,
            maxLatency = 150,
            executionsPerSec = 80
    )
    public void getAllTeams_durationPerformance_shouldPass() {
        logger.info("Running TeamService performance test: duration");

        List<Team> teams = teamService.getAllTeams();

        logger.info("Duration performance test returned {} teams", teams.size());

        assertFalse(teams.isEmpty());
    }

    /**
     * Intentionally failing performance test kept as evidence for manual testing.
     *
     * <p>This test is ignored by default because it is designed to fail with
     * unrealistic thresholds. It can be enabled manually to show how JUnitPerf
     * reports performance failures.</p>
     */
    @Ignore("Enable manually only to generate failing performance evidence for Sprint 2")
    @Test
    @JUnitPerfTest(
            threads = 10,
            durationMs = 3000,
            warmUpMs = 500
    )
    @JUnitPerfTestRequirement(
            meanLatency = 1,
            maxLatency = 2,
            executionsPerSec = 5000
    )
    public void getAllTeams_intentionallyFailingPerformanceTest() {
        logger.info("Running intentionally failing TeamService performance test");

        List<Team> teams = teamService.getAllTeams();

        logger.info("Intentionally failing performance test returned {} teams", teams.size());

        assertFalse(teams.isEmpty());
    }

    /**
     * Creates a dynamic proxy for repository interfaces used by TeamService.
     *
     * @param repositoryClass repository interface to proxy
     * @param teams list of teams returned by the fake findAll method
     * @param <T> repository type
     * @return fake repository implementation
     */
    @SuppressWarnings("unchecked")
    private <T> T createRepositoryProxy(Class<T> repositoryClass, List<Team> teams) {
        return (T) Proxy.newProxyInstance(
                repositoryClass.getClassLoader(),
                new Class<?>[]{repositoryClass},
                (proxy, method, args) -> {
                    String methodName = method.getName();

                    if (methodName.equals("findAll") && method.getParameterCount() == 0) {
                        simulateServerLatency();
                        return teams;
                    }

                    if (methodName.equals("toString")) {
                        return "Fake " + repositoryClass.getSimpleName();
                    }

                    if (methodName.equals("hashCode")) {
                        return System.identityHashCode(proxy);
                    }

                    if (methodName.equals("equals")) {
                        return proxy == args[0];
                    }

                    Class<?> returnType = method.getReturnType();

                    if (returnType.equals(Optional.class)) {
                        return Optional.empty();
                    }

                    if (returnType.equals(List.class)) {
                        return List.of();
                    }

                    if (returnType.equals(boolean.class)) {
                        return false;
                    }

                    if (returnType.equals(int.class)) {
                        return 0;
                    }

                    if (returnType.equals(long.class)) {
                        return 0L;
                    }

                    if (returnType.equals(void.class)) {
                        return null;
                    }

                    return null;
                }
        );
    }

    /**
     * Simulates a small service latency to make performance tests more realistic.
     */
    private void simulateServerLatency() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            logger.warn("Performance test latency simulation was interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Creates sample teams used during performance testing.
     *
     * @param amount number of teams to create
     * @return list of sample teams
     */
    private List<Team> createSampleTeams(int amount) {
        Country country = new Country();
        country.setId(1L);
        country.setName("Spain");

        List<Team> teams = new ArrayList<>();

        for (int i = 1; i <= amount; i++) {
            Team team = new Team();
            team.setId((long) i);
            team.setName("Team " + i);
            team.setLogoUrl("https://example.com/team-" + i + ".png");
            team.setCountry(country);

            teams.add(team);
        }

        return teams;
    }
}