package com.example.football_manager.repository;

import com.example.football_manager.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByTeamIdAndNumber(Long teamId, Integer number);

    boolean existsByTeamIdAndNumberAndIdNot(Long teamId, Integer number, Long id);

    List<Player> findByTeamIdOrderByNumberAsc(Long teamId);

    java.util.Optional<Player> findByIdAndTeamId(Long id, Long teamId);
}

