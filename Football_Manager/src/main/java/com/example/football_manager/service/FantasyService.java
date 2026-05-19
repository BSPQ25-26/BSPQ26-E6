package com.example.football_manager.service;

import com.example.football_manager.dto.*;
import com.example.football_manager.model.*;
import com.example.football_manager.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Service
public class FantasyService {

    private static final int MAX_LINEUP_SIZE = 11;
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final FantasyLeagueRepository fantasyLeagueRepository;
    private final FantasyLeagueMemberRepository fantasyLeagueMemberRepository;
    private final FantasyLineupPlayerRepository fantasyLineupPlayerRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    public FantasyService(
            FantasyLeagueRepository fantasyLeagueRepository,
            FantasyLeagueMemberRepository fantasyLeagueMemberRepository,
            FantasyLineupPlayerRepository fantasyLineupPlayerRepository,
            UserRepository userRepository,
            PlayerRepository playerRepository,
            MatchRepository matchRepository
    ) {
        this.fantasyLeagueRepository = fantasyLeagueRepository;
        this.fantasyLeagueMemberRepository = fantasyLeagueMemberRepository;
        this.fantasyLineupPlayerRepository = fantasyLineupPlayerRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional
    public FantasyLeagueDTO createLeague(Long userId, CreateFantasyLeagueRequestDTO request) {
        User owner = getUserOrThrow(userId);

        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("League name is required.");
        }

        FantasyLeague league = new FantasyLeague();
        league.setName(request.getName().trim());
        league.setCode(generateUniqueCode());
        league.setOwner(owner);

        FantasyLeague savedLeague = fantasyLeagueRepository.save(league);

        FantasyLeagueMember member = new FantasyLeagueMember();
        member.setLeague(savedLeague);
        member.setUser(owner);
        fantasyLeagueMemberRepository.save(member);

        return toLeagueDTO(savedLeague);
    }

    @Transactional
    public FantasyLeagueDTO joinLeague(Long userId, JoinFantasyLeagueRequestDTO request) {
        User user = getUserOrThrow(userId);

        if (request == null || request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("League code is required.");
        }

        FantasyLeague league = fantasyLeagueRepository
                .findByCodeIgnoreCase(request.getCode().trim())
                .orElseThrow(() -> new IllegalArgumentException("Fantasy league not found."));

        if (fantasyLeagueMemberRepository.existsByLeagueIdAndUserId(league.getId(), user.getId())) {
            throw new IllegalArgumentException("You are already a member of this fantasy league.");
        }

        FantasyLeagueMember member = new FantasyLeagueMember();
        member.setLeague(league);
        member.setUser(user);
        fantasyLeagueMemberRepository.save(member);

        return toLeagueDTO(league);
    }

    @Transactional(readOnly = true)
    public List<FantasyLeagueDTO> getMyLeagues(Long userId) {
        getUserOrThrow(userId);

        return fantasyLeagueMemberRepository.findByUserId(userId)
                .stream()
                .map(member -> toLeagueDTO(member.getLeague()))
                .sorted(Comparator.comparing(FantasyLeagueDTO::getName))
                .toList();
    }

    @Transactional(readOnly = true)
    public FantasyLeagueDTO getLeagueForMember(Long leagueId, Long userId) {
        FantasyLeague league = fantasyLeagueRepository.findById(leagueId)
                .orElseThrow(() -> new IllegalArgumentException("Fantasy league not found."));

        if (!fantasyLeagueMemberRepository.existsByLeagueIdAndUserId(leagueId, userId)) {
            throw new IllegalArgumentException("You are not a member of this fantasy league.");
        }

        return toLeagueDTO(league);
    }

    @Transactional(readOnly = true)
    public List<FantasyAvailablePlayerDTO> getAvailablePlayers() {
        return playerRepository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing((Player p) -> p.getTeam().getName())
                                .thenComparing(Player::getNumber)
                                .thenComparing(Player::getName)
                )
                .map(this::toAvailablePlayerDTO)
                .toList();
    }

    @Transactional
    public List<FantasyLineupPlayerDTO> saveLineup(Long userId, FantasyLineupRequestDTO request) {
        User user = getUserOrThrow(userId);

        if (request == null || request.getPlayerIds() == null || request.getPlayerIds().isEmpty()) {
            throw new IllegalArgumentException("Select at least one player.");
        }

        List<Long> playerIds = request.getPlayerIds();

        if (playerIds.size() > MAX_LINEUP_SIZE) {
            throw new IllegalArgumentException("A fantasy lineup cannot have more than 11 players.");
        }

        long distinctPlayers = playerIds.stream().distinct().count();
        if (distinctPlayers != playerIds.size()) {
            throw new IllegalArgumentException("A fantasy lineup cannot contain duplicated players.");
        }

        List<Player> players = playerRepository.findAllById(playerIds);

        if (players.size() != playerIds.size()) {
            throw new IllegalArgumentException("One or more selected players do not exist.");
        }

        validateLineupStructure(players);

        fantasyLineupPlayerRepository.deleteByUserId(userId);

        List<FantasyLineupPlayer> lineupPlayers = IntStream.range(0, playerIds.size())
                .mapToObj(index -> {
                    Long playerId = playerIds.get(index);

                    Player player = players.stream()
                            .filter(p -> p.getId().equals(playerId))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Player not found."));

                    FantasyLineupPlayer lineupPlayer = new FantasyLineupPlayer();
                    lineupPlayer.setUser(user);
                    lineupPlayer.setPlayer(player);
                    lineupPlayer.setLineupOrder(index + 1);

                    return lineupPlayer;
                })
                .toList();

        fantasyLineupPlayerRepository.saveAll(lineupPlayers);

        return getLineup(userId);
    }

    @Transactional(readOnly = true)
    public List<FantasyLineupPlayerDTO> getLineup(Long userId) {
        getUserOrThrow(userId);

        return fantasyLineupPlayerRepository.findByUserIdOrderByLineupOrderAsc(userId)
                .stream()
                .map(lineupPlayer -> {
                    Player player = lineupPlayer.getPlayer();
                    int points = calculatePlayerTotalPoints(player);

                    return toLineupPlayerDTO(lineupPlayer, points);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public FantasyScoreDTO getMyScore(Long userId) {
        User user = getUserOrThrow(userId);

        List<FantasyLineupPlayerDTO> lineup = getLineup(userId);

        int totalPoints = lineup.stream()
                .mapToInt(FantasyLineupPlayerDTO::getPoints)
                .sum();

        return new FantasyScoreDTO(
                user.getId(),
                user.getUsername(),
                totalPoints,
                lineup
        );
    }

    @Transactional(readOnly = true)
    public List<FantasyLeaderboardEntryDTO> getLeaderboard(Long leagueId) {
        FantasyLeague league = fantasyLeagueRepository.findById(leagueId)
                .orElseThrow(() -> new IllegalArgumentException("Fantasy league not found."));

        List<FantasyScoreDTO> scores = fantasyLeagueMemberRepository.findByLeagueId(league.getId())
                .stream()
                .map(member -> getMyScore(member.getUser().getId()))
                .sorted(
                        Comparator.comparing(FantasyScoreDTO::getTotalPoints).reversed()
                                .thenComparing(FantasyScoreDTO::getUsername)
                )
                .toList();

        return IntStream.range(0, scores.size())
                .mapToObj(index -> new FantasyLeaderboardEntryDTO(
                        index + 1,
                        scores.get(index).getUserId(),
                        scores.get(index).getUsername(),
                        scores.get(index).getTotalPoints()
                ))
                .toList();
    }

    private void validateLineupStructure(List<Player> players) {
        long goalkeepers = players.stream()
                .filter(player -> player.getPosition() == PlayerPosition.GOALKEEPER)
                .count();

        if (goalkeepers > 1) {
            throw new IllegalArgumentException("A fantasy lineup cannot contain more than one goalkeeper.");
        }
    }

    private int calculatePlayerTotalPoints(Player player) {
        return matchRepository.findAll()
                .stream()
                .filter(Match::isFinished)
                .filter(match -> playerAppearedInMatch(player, match))
                .mapToInt(match -> calculatePlayerMatchPoints(player, match))
                .sum();
    }

    private boolean playerAppearedInMatch(Player player, Match match) {
        Long playerTeamId = player.getTeam().getId();

        return match.getLeftTeam().getId().equals(playerTeamId)
                || match.getRightTeam().getId().equals(playerTeamId);
    }

    private int calculatePlayerMatchPoints(Player player, Match match) {
        boolean playerTeamIsLeftTeam = match.getLeftTeam().getId().equals(player.getTeam().getId());

        int teamGoals = playerTeamIsLeftTeam ? match.getLeftScore() : match.getRightScore();
        int opponentGoals = playerTeamIsLeftTeam ? match.getRightScore() : match.getLeftScore();

        int points = 2;

        if (teamGoals > opponentGoals) {
            points += 5;
        } else if (teamGoals == opponentGoals) {
            points += 2;
        }

        points += teamGoals - opponentGoals;

        if (opponentGoals == 0) {
            points += cleanSheetBonus(player.getPosition());
        }

        points += attackingBonus(player.getPosition(), teamGoals);
        points -= goalsConcededPenalty(player.getPosition(), opponentGoals);

        return Math.max(points, 0);
    }

    private int cleanSheetBonus(PlayerPosition position) {
        return switch (position) {
            case GOALKEEPER -> 5;
            case DEFENDER -> 4;
            case MIDFIELDER -> 1;
            case FORWARD -> 0;
        };
    }

    private int attackingBonus(PlayerPosition position, int teamGoals) {
        return switch (position) {
            case FORWARD -> teamGoals * 2;
            case MIDFIELDER -> teamGoals;
            case DEFENDER -> teamGoals > 0 ? 1 : 0;
            case GOALKEEPER -> 0;
        };
    }

    private int goalsConcededPenalty(PlayerPosition position, int opponentGoals) {
        return switch (position) {
            case GOALKEEPER, DEFENDER -> opponentGoals / 2;
            case MIDFIELDER, FORWARD -> 0;
        };
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private String generateUniqueCode() {
        String code;

        do {
            code = IntStream.range(0, 8)
                    .mapToObj(i -> String.valueOf(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length()))))
                    .reduce("", String::concat)
                    .toUpperCase(Locale.ROOT);
        } while (fantasyLeagueRepository.existsByCodeIgnoreCase(code));

        return code;
    }

    private FantasyLeagueDTO toLeagueDTO(FantasyLeague league) {
        return new FantasyLeagueDTO(
                league.getId(),
                league.getName(),
                league.getCode(),
                league.getOwner().getId(),
                league.getOwner().getUsername()
        );
    }

    private FantasyAvailablePlayerDTO toAvailablePlayerDTO(Player player) {
        Team team = player.getTeam();

        return new FantasyAvailablePlayerDTO(
                player.getId(),
                player.getName(),
                player.getNumber(),
                player.getPosition(),
                team.getId(),
                team.getName(),
                team.getLogoUrl()
        );
    }

    private FantasyLineupPlayerDTO toLineupPlayerDTO(FantasyLineupPlayer lineupPlayer, int points) {
        Player player = lineupPlayer.getPlayer();
        Team team = player.getTeam();

        return new FantasyLineupPlayerDTO(
                player.getId(),
                player.getName(),
                player.getNumber(),
                player.getPosition(),
                team.getId(),
                team.getName(),
                lineupPlayer.getLineupOrder(),
                points
        );
    }
}