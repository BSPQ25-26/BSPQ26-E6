package com.example.football_manager.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MatchRequestDTO {

    public enum MatchStatus {
        SCHEDULED,
        IN_PROGRESS,
        FINISHED,
        CANCELLED
    }

    private Long homeTeamId;
    private Long awayTeamId;
    private Long competitionId;
    private LocalDateTime kickoffTime;
    private String venue;
    
    private MatchStatus status;

    private Integer homeScore;
    private Integer awayScore;


    public Long getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(Long homeTeamId) { this.homeTeamId = homeTeamId; }

    public Long getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(Long awayTeamId) { this.awayTeamId = awayTeamId; }

    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }

    public LocalDateTime getKickoffTime() { return kickoffTime; }
    public void setKickoffTime(LocalDateTime kickoffTime) { this.kickoffTime = kickoffTime; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public Integer getHomeScore() { return homeScore; }
    public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }

    public Integer getAwayScore() { return awayScore; }
    public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }
}
