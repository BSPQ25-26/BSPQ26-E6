package com.example.football_manager.repository;

import com.example.football_manager.model.FantasyLeagueMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FantasyLeagueMemberRepository extends JpaRepository<FantasyLeagueMember, Long> {

    boolean existsByLeagueIdAndUserId(Long leagueId, Long userId);

    List<FantasyLeagueMember> findByLeagueId(Long leagueId);

    List<FantasyLeagueMember> findByUserId(Long userId);
}