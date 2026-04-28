package com.example.football_manager.dto;

import java.time.OffsetDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

public record MatchResultDTO(
        @Schema(description = "Match identifier", example = "220") Long matchId,
        @Schema(description = "Home team name", example = "Real Sociedad") String homeTeamName,
        @Schema(description = "Away team name", example = "Athletic Club") String awayTeamName,
        @Schema(description = "Home team score", example = "2") short homeScore,
        @Schema(description = "Away team score", example = "1") short awayScore,
        @Schema(description = "Kickoff time (UTC)", example = "2026-04-28T18:30:00Z") OffsetDateTime kickoffTime
) {
}
