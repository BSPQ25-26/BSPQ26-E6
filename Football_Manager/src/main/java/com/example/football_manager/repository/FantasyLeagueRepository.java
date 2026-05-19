package com.example.football_manager.repository;

import com.example.football_manager.model.FantasyLeague;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FantasyLeagueRepository extends JpaRepository<FantasyLeague, Long> {

    Optional<FantasyLeague> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}