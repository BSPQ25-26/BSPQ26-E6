package com.example.football_manager.repository;

import com.example.football_manager.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
}

