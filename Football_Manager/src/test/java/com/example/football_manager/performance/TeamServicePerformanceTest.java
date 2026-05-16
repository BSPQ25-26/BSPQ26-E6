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

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;

public class TeamServicePerformanceTest {

    @Rule
    public JUnitPerfRule perfTestRule = new JUnitPerfRule();

    private TeamService teamService;

    @Before
    public void setUp() {
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
    }

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
        List<Team> teams = teamService.getAllTeams();

        assertFalse(teams.isEmpty());
    }

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
        List<Team> teams = teamService.getAllTeams();

        assertFalse(teams.isEmpty());
    }

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
        List<Team> teams = teamService.getAllTeams();

        assertFalse(teams.isEmpty());
    }

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
        List<Team> teams = teamService.getAllTeams();

        assertFalse(teams.isEmpty());
    }

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
        List<Team> teams = teamService.getAllTeams();

        assertFalse(teams.isEmpty());
    }

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

    private void simulateServerLatency() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

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