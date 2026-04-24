package com.example.football_manager.repository;

import com.example.football_manager.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

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