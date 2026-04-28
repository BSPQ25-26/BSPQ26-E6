package com.example.football_manager.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public class MatchRequestDTO {

    public enum MatchStatus {
        SCHEDULED,
        IN_PROGRESS,
        FINISHED,
        CANCELLED
    }

    @Schema(description = "Home team identifier", example = "12")
    private Long homeTeamId;
    @Schema(description = "Away team identifier", example = "45")
    private Long awayTeamId;
    @Schema(description = "Competition identifier", example = "3")
    private Long competitionId;
    @Schema(description = "Kickoff time (local server time)", example = "2026-04-28T18:30:00")
    private LocalDateTime kickoffTime;
    @Schema(description = "Match venue or stadium name", example = "Anoeta Stadium")
    private String venue;
    
    @Schema(description = "Match status", example = "SCHEDULED")
    private MatchStatus status;

    @Schema(description = "Home team score", example = "2")
    private Integer homeScore;
    @Schema(description = "Away team score", example = "1")
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
