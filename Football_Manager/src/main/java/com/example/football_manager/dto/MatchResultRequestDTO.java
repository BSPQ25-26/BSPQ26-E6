package com.example.football_manager.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class MatchResultRequestDTO {

    @NotNull
    @Schema(description = "List of goals scored during the match")
    private List<@Valid GoalDTO> goals;

    public List<GoalDTO> getGoals() {
        return goals;
    }

    public void setGoals(List<GoalDTO> goals) {
        this.goals = goals;
    }

    public static class GoalDTO {

        @NotNull
        @Schema(description = "Identifier of the scoring team", example = "12")
        private Long teamId;

        @NotNull
        @Min(1)
        @Max(120)
        @Schema(description = "Minute of the goal (1-120)", example = "67")
        private Integer minute;

        @Min(0)
        @Max(30)
        @Schema(description = "Stoppage minute (optional)", example = "2")
        private Integer stoppageMinute;

        public Long getTeamId() {
            return teamId;
        }

        public void setTeamId(Long teamId) {
            this.teamId = teamId;
        }

        public Integer getMinute() {
            return minute;
        }

        public void setMinute(Integer minute) {
            this.minute = minute;
        }

        public Integer getStoppageMinute() {
            return stoppageMinute;
        }

        public void setStoppageMinute(Integer stoppageMinute) {
            this.stoppageMinute = stoppageMinute;
        }
    }
}
