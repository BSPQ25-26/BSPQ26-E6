package com.example.football_manager.repository;

import com.example.football_manager.model.MatchGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchGoalRepository extends JpaRepository<MatchGoal, Long> {

    void deleteByMatchId(Long matchId);

    List<MatchGoal> findByMatchIdOrderByMinuteAscStoppageMinuteAscIdAsc(Long matchId);
}
