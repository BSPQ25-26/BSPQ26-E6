package com.example.football_manager.service;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.dto.MatchResultDTO;
import com.example.football_manager.dto.MatchResultRequestDTO;
import com.example.football_manager.model.Competition;
import com.example.football_manager.model.Match;
import com.example.football_manager.model.MatchGoal;
import com.example.football_manager.model.Team;
import com.example.football_manager.repository.CompetitionRepository;
import com.example.football_manager.repository.MatchGoalRepository;
import com.example.football_manager.repository.MatchRepository;
import com.example.football_manager.repository.TeamRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class MatchService {
    
    private final MatchRepository matchRepository;
    private final MatchGoalRepository matchGoalRepository;
    private final TeamRepository teamRepository;
    private final CompetitionRepository competitionRepository;

    
    public MatchService(
            MatchRepository matchRepository,
            MatchGoalRepository matchGoalRepository,
            TeamRepository teamRepository,
            CompetitionRepository competitionRepository
    ) {
        this.matchRepository = matchRepository;
        this.matchGoalRepository = matchGoalRepository;
        this.teamRepository = teamRepository;
        this.competitionRepository = competitionRepository;
    }

    @Transactional
    public String createMatch(MatchRequestDTO request) {
        validateMatchRequest(request, true);

        Team homeTeam = teamRepository.findById(request.getHomeTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Home team not found with id: " + request.getHomeTeamId()));

        Team awayTeam = teamRepository.findById(request.getAwayTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Away team not found with id: " + request.getAwayTeamId()));

        Competition competition = competitionRepository.findById(request.getCompetitionId())
                .orElseThrow(() -> new IllegalArgumentException("Competition not found with id: " + request.getCompetitionId()));

        Match match = new Match();
        match.setLeftTeam(homeTeam);
        match.setRightTeam(awayTeam);
        match.setCompetition(competition);
        match.setDatetime(request.getKickoffTime().atZone(ZoneId.systemDefault()).toOffsetDateTime());
        match.setVenue(request.getVenue().trim());
        match.setLeftScore(toShortScore(request.getHomeScore()));
        match.setRightScore(toShortScore(request.getAwayScore()));
        match.setFinished(request.getStatus() == MatchRequestDTO.MatchStatus.FINISHED);

        matchRepository.save(match);

        return "Match scheduled successfully.";
    }

    private void validateMatchRequest(MatchRequestDTO request, boolean competitionRequired) {
        if (request == null) {
            throw new IllegalArgumentException("Validation Error: Match data is required.");
        }

        if (request.getHomeTeamId() == null || request.getAwayTeamId() == null) {
            throw new IllegalArgumentException("Validation Error: Both Home and Away team IDs are required.");
        }

        if (competitionRequired && request.getCompetitionId() == null) {
            throw new IllegalArgumentException("Validation Error: Competition ID is required.");
        }

        if (request.getKickoffTime() == null || request.getVenue() == null) {
            throw new IllegalArgumentException("Validation Error: Kickoff time and Venue are required.");
        }

        if (request.getHomeTeamId().equals(request.getAwayTeamId())) {
            throw new IllegalArgumentException("Validation Error: Home and Away teams must be different.");
        }

        if (request.getStatus() == null) {
            request.setStatus(MatchRequestDTO.MatchStatus.SCHEDULED);
        }
    }

    @Transactional
    public String updateMatch(Long id, MatchRequestDTO request) {
        validateMatchUpdate(request);

        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        Team homeTeam = teamRepository.findById(request.getHomeTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Home team not found"));
        Team awayTeam = teamRepository.findById(request.getAwayTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Away team not found"));

        match.setLeftTeam(homeTeam);
        match.setRightTeam(awayTeam);
        
        // Actualizamos la fecha usando la zona horaria del sistema de tu compañero
        if (request.getKickoffTime() != null) {
            match.setDatetime(request.getKickoffTime().atZone(ZoneId.systemDefault()).toOffsetDateTime());
        }

        // Actualizamos la sede (venue)
        if (request.getVenue() != null && !request.getVenue().isBlank()) {
            match.setVenue(request.getVenue().trim());
        }

        match.setFinished(request.getStatus() == MatchRequestDTO.MatchStatus.FINISHED);

        if (request.getHomeScore() != null) {
            match.setLeftScore(request.getHomeScore().shortValue());
        }
        if (request.getAwayScore() != null) {
            match.setRightScore(request.getAwayScore().shortValue());
        }

        matchRepository.save(match);

        return "Match with ID " + id + " has been updated.";
    }

    @Transactional
    public String deleteMatch(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + id));

        matchGoalRepository.deleteByMatchId(id);
        matchRepository.delete(match);

        return "Match with ID " + id + " has been deleted.";
    }

    @Transactional
    public String registerResult(Long id, MatchResultRequestDTO request) {
        if (request == null || request.getGoals() == null) {
            throw new IllegalArgumentException("Validation Error: Goals are required.");
        }

        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + id));

        List<MatchGoal> goals = new ArrayList<>();
        int leftScore = 0;
        int rightScore = 0;

        for (MatchResultRequestDTO.GoalDTO goalDTO : request.getGoals()) {
            Team scoringTeam = resolveScoringTeam(match, goalDTO);

            if (Objects.equals(scoringTeam.getId(), match.getLeftTeam().getId())) {
                leftScore++;
            } else {
                rightScore++;
            }

            MatchGoal goal = new MatchGoal();
            goal.setMatch(match);
            goal.setTeam(scoringTeam);
            goal.setMinute(goalDTO.getMinute().shortValue());
            goal.setStoppageMinute(toShort(goalDTO.getStoppageMinute()));
            goals.add(goal);
        }

        matchGoalRepository.deleteByMatchId(id);
        matchGoalRepository.saveAll(goals);

        match.setLeftScore((short) leftScore);
        match.setRightScore((short) rightScore);
        match.setFinished(true);
        matchRepository.save(match);

        return "Result registered for match " + id + ": " + leftScore + " - " + rightScore;
    }

    public List<MatchResultDTO> getFinishedMatchResults() {
        List<Match> matches = matchRepository.findByFinishedTrueOrderByDatetimeDesc();

        return matches.stream()
                .map(match -> new MatchResultDTO(
                        match.getId(),
                        match.getLeftTeam().getName(),
                        match.getRightTeam().getName(),
                        match.getLeftScore(),
                        match.getRightScore(),
                        match.getDatetime()
                ))
                .toList();
    }
    
    public Optional<MatchRequestDTO> getMatchForEdit(Long id) {
        if (matchRepository == null) {
            return Optional.empty();
        }

        return matchRepository.findById(id).map(match -> {
            MatchRequestDTO dto = new MatchRequestDTO();
            dto.setHomeTeamId(match.getLeftTeam().getId());
            dto.setAwayTeamId(match.getRightTeam().getId());
            dto.setKickoffTime(match.getDatetime().toLocalDateTime());
            dto.setVenue(match.getVenue()); // Carga el venue real, no "TBD"
            dto.setStatus(match.isFinished()
                    ? MatchRequestDTO.MatchStatus.FINISHED
                    : MatchRequestDTO.MatchStatus.SCHEDULED);
            dto.setHomeScore((int) match.getLeftScore());
            dto.setAwayScore((int) match.getRightScore());
            return dto;
        });
    }

    private void validateMatchUpdate(MatchRequestDTO request) {
        if (request.getHomeTeamId() == null || request.getAwayTeamId() == null) {
            throw new IllegalArgumentException("Validation Error: Both Home and Away team IDs are required.");
        }

        if (request.getKickoffTime() == null || request.getVenue() == null || request.getVenue().isBlank()) {
            throw new IllegalArgumentException("Validation Error: Kickoff time and Venue are required.");
        }

        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Validation Error: Match status is required.");
        }

        if (request.getHomeTeamId().equals(request.getAwayTeamId())) {
            throw new IllegalArgumentException("Validation Error: Home and Away teams must be different.");
        }

        if (request.getHomeScore() != null && request.getAwayScore() == null) {
            throw new IllegalArgumentException("Validation Error: Away score is required when home score is provided.");
        }

        if (request.getAwayScore() != null && request.getHomeScore() == null) {
            throw new IllegalArgumentException("Validation Error: Home score is required when away score is provided.");
        }

        if (request.getStatus() == MatchRequestDTO.MatchStatus.FINISHED
                && (request.getHomeScore() == null || request.getAwayScore() == null)) {
            throw new IllegalArgumentException("Validation Error: Both scores are required when status is FINISHED.");
        }

        if (request.getStatus() != MatchRequestDTO.MatchStatus.FINISHED
                && (request.getHomeScore() != null || request.getAwayScore() != null)) {
            throw new IllegalArgumentException("Validation Error: Scores can only be submitted when status is FINISHED.");
        }
    } // <-- ESTA ES LA LLAVE QUE FALTABA

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + id));
    }

    public List<MatchGoal> getGoalsByMatchId(Long matchId) {
        return matchGoalRepository.findByMatchIdOrderByMinuteAscStoppageMinuteAscIdAsc(matchId);
    }

    public List<Match> getRecentMatchesByTeamId(Long teamId) {
        return matchRepository
                .findByFinishedTrueAndLeftTeamIdOrFinishedTrueAndRightTeamIdOrderByDatetimeDesc(
                        teamId,
                        teamId
                );
    }

    public List<Match> getFutureMatchesByTeamId(Long teamId) {
        OffsetDateTime now = OffsetDateTime.now();

        return matchRepository
                .findByFinishedFalseAndDatetimeAfterAndLeftTeamIdOrFinishedFalseAndDatetimeAfterAndRightTeamIdOrderByDatetimeAsc(
                        now,
                        teamId,
                        now,
                        teamId
                );
    }

    public List<Match> getFutureMatchesByFavouriteTeamIds(Set<Long> favouriteTeamIds) {
        if (favouriteTeamIds == null || favouriteTeamIds.isEmpty()) {
            return List.of();
        }

        OffsetDateTime now = OffsetDateTime.now();

        return matchRepository
                .findByFinishedFalseAndDatetimeAfterAndLeftTeamIdInOrFinishedFalseAndDatetimeAfterAndRightTeamIdInOrderByDatetimeAsc(
                        now,
                        favouriteTeamIds,
                        now,
                        favouriteTeamIds
                );
    }

    private Team resolveScoringTeam(Match match, MatchResultRequestDTO.GoalDTO goalDTO) {
        if (goalDTO == null || goalDTO.getTeamId() == null || goalDTO.getMinute() == null) {
            throw new IllegalArgumentException("Validation Error: Every goal must include team ID and minute.");
        }

        if (goalDTO.getMinute() < 1 || goalDTO.getMinute() > 120) {
            throw new IllegalArgumentException("Validation Error: Goal minute must be between 1 and 120.");
        }

        if (goalDTO.getStoppageMinute() != null
                && (goalDTO.getStoppageMinute() < 0 || goalDTO.getStoppageMinute() > 30)) {
            throw new IllegalArgumentException("Validation Error: Stoppage minute must be between 0 and 30.");
        }

        if (Objects.equals(goalDTO.getTeamId(), match.getLeftTeam().getId())) {
            return match.getLeftTeam();
        }

        if (Objects.equals(goalDTO.getTeamId(), match.getRightTeam().getId())) {
            return match.getRightTeam();
        }

        throw new IllegalArgumentException("Validation Error: Scoring team must belong to the match.");
    }

    private Short toShort(Integer value) {
        return value == null ? null : value.shortValue();
    }

    private short toShortScore(Integer score) {
        if (score == null) {
            return 0;
        }

        if (score < 0 || score > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Validation Error: Score must be a positive number.");
        }

        return score.shortValue();
    }
}