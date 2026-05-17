package com.example.football_manager.repository;

import com.example.football_manager.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("""
            SELECT m FROM Match m
            WHERE (:teamId IS NULL OR m.leftTeam.id = :teamId OR m.rightTeam.id = :teamId)
              AND (:finished IS NULL OR m.finished = :finished)
              AND (:competitionId IS NULL OR m.competition.id = :competitionId)
            """)
    List<Match> findByFilters(
            @Param("teamId") Long teamId,
            @Param("finished") Boolean finished,
            @Param("competitionId") Long competitionId
    );

    List<Match> findByFinishedTrueOrderByDatetimeDesc();

    List<Match> findByFinishedTrueAndLeftTeamIdOrFinishedTrueAndRightTeamIdOrderByDatetimeDesc(
            Long leftTeamId,
            Long rightTeamId
    );

    List<Match> findByFinishedFalseAndDatetimeAfterAndLeftTeamIdOrFinishedFalseAndDatetimeAfterAndRightTeamIdOrderByDatetimeAsc(
            OffsetDateTime now1,
            Long leftTeamId,
            OffsetDateTime now2,
            Long rightTeamId
    );

    List<Match> findByFinishedFalseAndDatetimeAfterAndLeftTeamIdInOrFinishedFalseAndDatetimeAfterAndRightTeamIdInOrderByDatetimeAsc(
            OffsetDateTime now1,
            Collection<Long> leftTeamIds,
            OffsetDateTime now2,
            Collection<Long> rightTeamIds
    );
}
