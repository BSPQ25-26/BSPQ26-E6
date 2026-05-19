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

/**
 * Service responsible for managing the Fantasy mode of the application.
 *
 * <p>This service contains the main business logic for Fantasy leagues:
 * creating leagues, joining leagues with invite codes, selecting lineups,
 * calculating fantasy points and generating league leaderboards.</p>
 *
 * <p>Fantasy points are calculated from finished football matches using
 * team result, goal difference, clean sheets and player position bonuses.</p>
 */
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

    /**
     * Creates a new Fantasy service instance with all required repositories.
     *
     * @param fantasyLeagueRepository repository used to manage fantasy leagues
     * @param fantasyLeagueMemberRepository repository used to manage league memberships
     * @param fantasyLineupPlayerRepository repository used to manage user fantasy lineups
     * @param userRepository repository used to access users
     * @param playerRepository repository used to access players
     * @param matchRepository repository used to access football matches
     */
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

    /**
     * Creates a new fantasy league and automatically adds the creator as a member.
     *
     * @param userId id of the user creating the fantasy league
     * @param request request containing the league name
     * @return created fantasy league data
     * @throws IllegalArgumentException if the user does not exist or the league name is invalid
     */
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

    /**
     * Adds a user to an existing fantasy league using an invite code.
     *
     * @param userId id of the user joining the league
     * @param request request containing the fantasy league invite code
     * @return joined fantasy league data
     * @throws IllegalArgumentException if the user does not exist, the code is invalid,
     *                                  or the user is already a league member
     */
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

    /**
     * Returns all fantasy leagues where a user is a member.
     *
     * @param userId id of the user
     * @return list of fantasy leagues joined by the user
     * @throws IllegalArgumentException if the user does not exist
     */
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

    /**
     * Returns a specific fantasy league only if the user belongs to it.
     *
     * @param leagueId id of the fantasy league
     * @param userId id of the user requesting the league
     * @return fantasy league data
     * @throws IllegalArgumentException if the league does not exist or the user is not a member
     */
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

    /**
     * Returns the list of players available for fantasy lineup selection.
     *
     * @return sorted list of available fantasy players
     */
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

    /**
     * Saves the fantasy lineup selected by a user.
     *
     * <p>The lineup can contain up to eleven players, cannot include duplicated
     * players and cannot include more than one goalkeeper.</p>
     *
     * @param userId id of the user saving the lineup
     * @param request request containing selected player ids
     * @return saved lineup including calculated fantasy points
     * @throws IllegalArgumentException if the user does not exist or the lineup is invalid
     */
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

    /**
     * Returns the current fantasy lineup of a user.
     *
     * @param userId id of the user
     * @return ordered fantasy lineup with points for each player
     * @throws IllegalArgumentException if the user does not exist
     */
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

    /**
     * Calculates the total fantasy score of a user from the selected lineup.
     *
     * @param userId id of the user
     * @return fantasy score summary with total points and selected lineup
     * @throws IllegalArgumentException if the user does not exist
     */
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

    /**
     * Generates the leaderboard of a fantasy league.
     *
     * <p>The leaderboard is ordered by total fantasy points in descending order.
     * If users have the same points, they are sorted by username.</p>
     *
     * @param leagueId id of the fantasy league
     * @return ordered leaderboard entries
     * @throws IllegalArgumentException if the fantasy league does not exist
     */
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

    /**
     * Validates basic fantasy lineup restrictions.
     *
     * @param players selected players
     * @throws IllegalArgumentException if more than one goalkeeper is selected
     */
    private void validateLineupStructure(List<Player> players) {
        long goalkeepers = players.stream()
                .filter(player -> player.getPosition() == PlayerPosition.GOALKEEPER)
                .count();

        if (goalkeepers > 1) {
            logger.warn("Fantasy lineup validation failed because more than one goalkeeper was selected");
            throw new IllegalArgumentException("A fantasy lineup cannot contain more than one goalkeeper.");
        }
    }

    /**
     * Calculates the total fantasy points of a player across all finished matches.
     *
     * @param player player whose fantasy points are calculated
     * @return total fantasy points
     */
    private int calculatePlayerTotalPoints(Player player) {
        return matchRepository.findAll()
                .stream()
                .filter(Match::isFinished)
                .filter(match -> playerAppearedInMatch(player, match))
                .mapToInt(match -> calculatePlayerMatchPoints(player, match))
                .sum();
    }

    /**
     * Checks whether a player belongs to one of the two teams in a match.
     *
     * @param player player to check
     * @param match match to check
     * @return true if the player team participated in the match
     */
    private boolean playerAppearedInMatch(Player player, Match match) {
        Long playerTeamId = player.getTeam().getId();

        return match.getLeftTeam().getId().equals(playerTeamId)
                || match.getRightTeam().getId().equals(playerTeamId);
    }

    /**
     * Calculates the fantasy points of a player for a single finished match.
     *
     * @param player player whose points are calculated
     * @param match match used as source of performance data
     * @return fantasy points obtained in that match
     */
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

    /**
     * Returns the clean sheet bonus depending on the player position.
     *
     * @param position player position
     * @return clean sheet bonus
     */
    private int cleanSheetBonus(PlayerPosition position) {
        return switch (position) {
            case GOALKEEPER -> 5;
            case DEFENDER -> 4;
            case MIDFIELDER -> 1;
            case FORWARD -> 0;
        };
    }

    /**
     * Returns attacking bonus points depending on the player position and team goals.
     *
     * @param position player position
     * @param teamGoals goals scored by the player's team
     * @return attacking bonus points
     */
    private int attackingBonus(PlayerPosition position, int teamGoals) {
        return switch (position) {
            case FORWARD -> teamGoals * 2;
            case MIDFIELDER -> teamGoals;
            case DEFENDER -> teamGoals > 0 ? 1 : 0;
            case GOALKEEPER -> 0;
        };
    }

    /**
     * Returns penalty points for goals conceded depending on the player position.
     *
     * @param position player position
     * @param opponentGoals goals conceded by the player's team
     * @return penalty points
     */
    private int goalsConcededPenalty(PlayerPosition position, int opponentGoals) {
        return switch (position) {
            case GOALKEEPER, DEFENDER -> opponentGoals / 2;
            case MIDFIELDER, FORWARD -> 0;
        };
    }

    /**
     * Finds a user by id or throws an exception if it does not exist.
     *
     * @param userId id of the user
     * @return found user
     * @throws IllegalArgumentException if the user does not exist
     */
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User id={} was not found", userId);
                    return new IllegalArgumentException("User not found.");
                });
    }

    /**
     * Generates a unique invite code for a fantasy league.
     *
     * @return unique uppercase fantasy league code
     */
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

    /**
     * Converts a FantasyLeague entity into a DTO.
     *
     * @param league fantasy league entity
     * @return fantasy league DTO
     */
    private FantasyLeagueDTO toLeagueDTO(FantasyLeague league) {
        return new FantasyLeagueDTO(
                league.getId(),
                league.getName(),
                league.getCode(),
                league.getOwner().getId(),
                league.getOwner().getUsername()
        );
    }

    /**
     * Converts a Player entity into a fantasy available player DTO.
     *
     * @param player player entity
     * @return available player DTO
     */
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

    /**
     * Converts a FantasyLineupPlayer entity into a lineup DTO.
     *
     * @param lineupPlayer lineup player entity
     * @param points calculated fantasy points
     * @return lineup player DTO
     */
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