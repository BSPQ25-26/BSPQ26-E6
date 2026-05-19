package com.example.football_manager.service;

import com.example.football_manager.dto.*;
import com.example.football_manager.model.*;
import com.example.football_manager.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Service
public class FantasyService {

    private static final Logger logger = LoggerFactory.getLogger(FantasyService.class);

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
    @CacheEvict(cacheNames = "fantasyLeagues", key = "#userId")
    public FantasyLeagueDTO createLeague(Long userId, CreateFantasyLeagueRequestDTO request) {
        logger.info("Creating fantasy league for userId={}", userId);

        User owner = getUserOrThrow(userId);

        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            logger.warn("Fantasy league creation failed for userId={} because league name is empty", userId);
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

        logger.info(
                "Fantasy league created successfully with id={}, code={} and ownerId={}",
                savedLeague.getId(),
                savedLeague.getCode(),
                owner.getId()
        );

        return toLeagueDTO(savedLeague);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "fantasyLeagues", key = "#userId"),
            @CacheEvict(cacheNames = "fantasyLeaderboard", key = "#result.id")
    })
    public FantasyLeagueDTO joinLeague(Long userId, JoinFantasyLeagueRequestDTO request) {
        logger.info("User id={} is trying to join a fantasy league", userId);

        User user = getUserOrThrow(userId);

        if (request == null || request.getCode() == null || request.getCode().trim().isEmpty()) {
            logger.warn("Fantasy league join failed for userId={} because league code is empty", userId);
            throw new IllegalArgumentException("League code is required.");
        }

        String normalizedCode = request.getCode().trim();

        FantasyLeague league = fantasyLeagueRepository
                .findByCodeIgnoreCase(normalizedCode)
                .orElseThrow(() -> {
                    logger.warn(
                            "Fantasy league join failed for userId={} because code={} was not found",
                            userId,
                            normalizedCode
                    );
                    return new IllegalArgumentException("Fantasy league not found.");
                });

        if (fantasyLeagueMemberRepository.existsByLeagueIdAndUserId(league.getId(), user.getId())) {
            logger.warn(
                    "Fantasy league join failed because userId={} is already member of leagueId={}",
                    userId,
                    league.getId()
            );
            throw new IllegalArgumentException("You are already a member of this fantasy league.");
        }

        FantasyLeagueMember member = new FantasyLeagueMember();
        member.setLeague(league);
        member.setUser(user);
        fantasyLeagueMemberRepository.save(member);

        logger.info("User id={} joined fantasy league id={}", userId, league.getId());

        return toLeagueDTO(league);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "fantasyLeagues", key = "#userId")
    public List<FantasyLeagueDTO> getMyLeagues(Long userId) {
        logger.info("Fetching fantasy leagues for userId={}", userId);

        getUserOrThrow(userId);

        return fantasyLeagueMemberRepository.findByUserId(userId)
                .stream()
                .map(member -> toLeagueDTO(member.getLeague()))
                .sorted(Comparator.comparing(FantasyLeagueDTO::getName))
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "fantasyLeague", key = "T(java.util.Objects).hash(#leagueId, #userId)")
    public FantasyLeagueDTO getLeagueForMember(Long leagueId, Long userId) {
        logger.info("Fetching fantasy league id={} for userId={}", leagueId, userId);

        FantasyLeague league = fantasyLeagueRepository.findById(leagueId)
                .orElseThrow(() -> {
                    logger.warn("Fantasy league id={} was not found", leagueId);
                    return new IllegalArgumentException("Fantasy league not found.");
                });

        if (!fantasyLeagueMemberRepository.existsByLeagueIdAndUserId(leagueId, userId)) {
            logger.warn("User id={} tried to access fantasy league id={} without membership", userId, leagueId);
            throw new IllegalArgumentException("You are not a member of this fantasy league.");
        }

        return toLeagueDTO(league);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "fantasyAvailablePlayers")
    public List<FantasyAvailablePlayerDTO> getAvailablePlayers() {
        logger.info("Fetching available fantasy players");

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
    @Caching(evict = {
            @CacheEvict(cacheNames = "fantasyLineup", key = "#userId"),
            @CacheEvict(cacheNames = "fantasyScore", key = "#userId"),
            @CacheEvict(cacheNames = "fantasyLeaderboard", allEntries = true)
    })
    public List<FantasyLineupPlayerDTO> saveLineup(Long userId, FantasyLineupRequestDTO request) {
        logger.info("Saving fantasy lineup for userId={}", userId);

        User user = getUserOrThrow(userId);

        if (request == null || request.getPlayerIds() == null || request.getPlayerIds().isEmpty()) {
            logger.warn("Fantasy lineup save failed for userId={} because no players were selected", userId);
            throw new IllegalArgumentException("Select at least one player.");
        }

        List<Long> playerIds = request.getPlayerIds();

        if (playerIds.size() > MAX_LINEUP_SIZE) {
            logger.warn(
                    "Fantasy lineup save failed for userId={} because {} players were selected",
                    userId,
                    playerIds.size()
            );
            throw new IllegalArgumentException("A fantasy lineup cannot have more than 11 players.");
        }

        long distinctPlayers = playerIds.stream().distinct().count();
        if (distinctPlayers != playerIds.size()) {
            logger.warn("Fantasy lineup save failed for userId={} because duplicated players were selected", userId);
            throw new IllegalArgumentException("A fantasy lineup cannot contain duplicated players.");
        }

        List<Player> players = playerRepository.findAllById(playerIds);

        if (players.size() != playerIds.size()) {
            logger.warn("Fantasy lineup save failed for userId={} because some selected players do not exist", userId);
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

        logger.info("Fantasy lineup saved for userId={} with {} players", userId, lineupPlayers.size());

        return getLineup(userId);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "fantasyLineup", key = "#userId")
    public List<FantasyLineupPlayerDTO> getLineup(Long userId) {
        logger.info("Fetching fantasy lineup for userId={}", userId);

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
    @Cacheable(cacheNames = "fantasyScore", key = "#userId")
    public FantasyScoreDTO getMyScore(Long userId) {
        logger.info("Calculating fantasy score for userId={}", userId);

        User user = getUserOrThrow(userId);

        List<FantasyLineupPlayerDTO> lineup = getLineup(userId);

        int totalPoints = lineup.stream()
                .mapToInt(FantasyLineupPlayerDTO::getPoints)
                .sum();

        logger.info("Fantasy score calculated for userId={} with totalPoints={}", userId, totalPoints);

        return new FantasyScoreDTO(
                user.getId(),
                user.getUsername(),
                totalPoints,
                lineup
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "fantasyLeaderboard", key = "#leagueId")
    public List<FantasyLeaderboardEntryDTO> getLeaderboard(Long leagueId) {
        logger.info("Generating fantasy leaderboard for leagueId={}", leagueId);

        FantasyLeague league = fantasyLeagueRepository.findById(leagueId)
                .orElseThrow(() -> {
                    logger.warn("Fantasy leaderboard generation failed because leagueId={} was not found", leagueId);
                    return new IllegalArgumentException("Fantasy league not found.");
                });

        List<FantasyScoreDTO> scores = fantasyLeagueMemberRepository.findByLeagueId(league.getId())
                .stream()
                .map(member -> getMyScore(member.getUser().getId()))
                .sorted(
                        Comparator.comparing(FantasyScoreDTO::getTotalPoints).reversed()
                                .thenComparing(FantasyScoreDTO::getUsername)
                )
                .toList();

        logger.info("Fantasy leaderboard generated for leagueId={} with {} members", leagueId, scores.size());

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
            logger.warn("Fantasy lineup validation failed because more than one goalkeeper was selected");
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
                .orElseThrow(() -> {
                    logger.warn("User id={} was not found", userId);
                    return new IllegalArgumentException("User not found.");
                });
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