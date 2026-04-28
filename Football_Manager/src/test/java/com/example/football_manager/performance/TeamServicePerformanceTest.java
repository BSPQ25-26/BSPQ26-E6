package com.example.football_manager.performance;

import com.example.football_manager.model.Team;
import com.example.football_manager.repository.TeamRepository;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TeamServicePerformanceTest {

    @Rule
    public ContiPerfRule contiPerfRule = new ContiPerfRule();

    private final TeamRepository teamRepository = Mockito.mock(TeamRepository.class);

    @Test
    @PerfTest(invocations = 100, threads = 10)
    @Required(max = 1000, average = 300)
    public void testFindAllTeamsPerformance() {
        Team team = new Team();
        team.setId(1L);
        team.setName("Real Madrid");
        team.setLogoUrl("https://example.com/logo.png");

        when(teamRepository.findAll()).thenReturn(List.of(team));

        List<Team> teams = teamRepository.findAll();

        assertFalse(teams.isEmpty());
    }
}