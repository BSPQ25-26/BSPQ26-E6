package com.example.football_manager.dto;

import java.time.OffsetDateTime;

public record MatchResultDTO(
        Long matchId,
        String homeTeamName,
        String awayTeamName,
        short homeScore,
        short awayScore,
        OffsetDateTime kickoffTime
) {
}
