package com.example.football_manager.repository;

import com.example.football_manager.model.FantasyLineupPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FantasyLineupPlayerRepository extends JpaRepository<FantasyLineupPlayer, Long> {

    List<FantasyLineupPlayer> findByUserIdOrderByLineupOrderAsc(Long userId);

    void deleteByUserId(Long userId);
}