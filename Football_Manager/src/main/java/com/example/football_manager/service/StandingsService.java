package com.example.football_manager.service;

import com.example.football_manager.dto.StandingDTO;
import com.example.football_manager.model.Competition;
import com.example.football_manager.model.Match;
import com.example.football_manager.model.Team;
import com.example.football_manager.repository.CompetitionRepository;
import com.example.football_manager.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StandingsService {

    private final CompetitionRepository competitionRepository;
    private final MatchRepository matchRepository;

    public StandingsService(CompetitionRepository competitionRepository,
                            MatchRepository matchRepository) {
        this.competitionRepository = competitionRepository;
        this.matchRepository = matchRepository;
    }

    public List<Competition> getCompetitions() {
        return competitionRepository.findAll();
    }

    public List<StandingDTO> getStandingsForCompetition(Long competitionId) {
        List<Match> matches = matchRepository.findByFilters(null, null, competitionId);

        Map<Long, Row> rowsByTeamId = new LinkedHashMap<>();
        for (Match match : matches) {
            Row left = rowsByTeamId.computeIfAbsent(
                    match.getLeftTeam().getId(), id -> new Row(match.getLeftTeam()));
            Row right = rowsByTeamId.computeIfAbsent(
                    match.getRightTeam().getId(), id -> new Row(match.getRightTeam()));

            if (!match.isFinished()) {
                continue;
            }

            int leftGoals = match.getLeftScore();
            int rightGoals = match.getRightScore();

            left.matchesPlayed++;
            right.matchesPlayed++;
            left.goalsFor += leftGoals;
            left.goalsAgainst += rightGoals;
            right.goalsFor += rightGoals;
            right.goalsAgainst += leftGoals;

            if (leftGoals > rightGoals) {
                left.won++;
                right.lost++;
            } else if (leftGoals < rightGoals) {
                right.won++;
                left.lost++;
            } else {
                left.drawn++;
                right.drawn++;
            }
        }

        List<StandingDTO> standings = new ArrayList<>(rowsByTeamId.size());
        for (Row row : rowsByTeamId.values()) {
            int goalDifference = row.goalsFor - row.goalsAgainst;
            int points = row.won * 3 + row.drawn;
            standings.add(new StandingDTO(
                    row.team.getName(),
                    row.team.getLogoUrl(),
                    row.matchesPlayed,
                    row.won,
                    row.drawn,
                    row.lost,
                    row.goalsFor,
                    row.goalsAgainst,
                    goalDifference,
                    points
            ));
        }

        standings.sort(Comparator
                .comparingInt(StandingDTO::getPoints).reversed()
                .thenComparing(Comparator.comparingInt(StandingDTO::getGoalDifference).reversed())
                .thenComparing(Comparator.comparingInt(StandingDTO::getGoalsFor).reversed())
                .thenComparing(StandingDTO::getTeamName));

        return standings;
    }

    private static class Row {
        final Team team;
        int matchesPlayed;
        int won;
        int drawn;
        int lost;
        int goalsFor;
        int goalsAgainst;

        Row(Team team) {
            this.team = team;
        }
    }
}
