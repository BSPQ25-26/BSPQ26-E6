package com.example.football_manager.acceptance;

import com.example.football_manager.dto.CreateFantasyLeagueRequestDTO;
import com.example.football_manager.dto.FantasyAvailablePlayerDTO;
import com.example.football_manager.dto.FantasyLeagueDTO;
import com.example.football_manager.dto.FantasyLineupPlayerDTO;
import com.example.football_manager.dto.FantasyLineupRequestDTO;
import com.example.football_manager.dto.FantasyScoreDTO;
import com.example.football_manager.dto.JoinFantasyLeagueRequestDTO;
import com.example.football_manager.model.PlayerPosition;
import com.example.football_manager.service.FantasyService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:fantasyacceptancetest;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc(addFilters = false)
class FantasyAcceptanceTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FantasyService fantasyService;

    @Test
    void unauthenticatedUser_whenOpeningFantasy_shouldBeRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/fantasy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }

    @Test
    void loggedUser_whenOpeningFantasyDashboard_shouldSeeFantasyPage() throws Exception {
        FantasyLeagueDTO league = sampleLeague();
        FantasyAvailablePlayerDTO availablePlayer = sampleAvailablePlayer();
        FantasyLineupPlayerDTO lineupPlayer = sampleLineupPlayer();

        when(fantasyService.getMyLeagues(USER_ID)).thenReturn(List.of(league));
        when(fantasyService.getLeaderboard(league.getId())).thenReturn(List.of());
        when(fantasyService.getAvailablePlayers()).thenReturn(List.of(availablePlayer));
        when(fantasyService.getLineup(USER_ID)).thenReturn(List.of(lineupPlayer));
        when(fantasyService.getMyScore(USER_ID))
                .thenReturn(new FantasyScoreDTO(USER_ID, "acceptance_user", 25, List.of(lineupPlayer)));

        mockMvc.perform(get("/fantasy")
                        .sessionAttr("userId", USER_ID)
                        .sessionAttr("isAdmin", false))
                .andExpect(status().isOk())
                .andExpect(view().name("fantasy"))
                .andExpect(model().attributeExists("myLeagues"))
                .andExpect(model().attributeExists("selectedLeague"))
                .andExpect(model().attributeExists("leaderboard"))
                .andExpect(model().attributeExists("availablePlayers"))
                .andExpect(model().attributeExists("lineup"))
                .andExpect(model().attributeExists("myScore"));
    }

    @Test
    void loggedUser_whenCreatingFantasyLeague_shouldBeRedirectedToCreatedLeague() throws Exception {
        FantasyLeagueDTO createdLeague = sampleLeague();

        when(fantasyService.createLeague(eq(USER_ID), any(CreateFantasyLeagueRequestDTO.class)))
                .thenReturn(createdLeague);

        mockMvc.perform(post("/fantasy/leagues")
                        .sessionAttr("userId", USER_ID)
                        .sessionAttr("isAdmin", false)
                        .param("name", "Liga E6 Fantasy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fantasy/leagues/" + createdLeague.getId()))
                .andExpect(flash().attributeExists("successMessage"));

        verify(fantasyService).createLeague(eq(USER_ID), any(CreateFantasyLeagueRequestDTO.class));
    }

    @Test
    void loggedUser_whenJoiningFantasyLeagueWithCode_shouldBeRedirectedToJoinedLeague() throws Exception {
        FantasyLeagueDTO joinedLeague = sampleLeague();

        when(fantasyService.joinLeague(eq(USER_ID), any(JoinFantasyLeagueRequestDTO.class)))
                .thenReturn(joinedLeague);

        mockMvc.perform(post("/fantasy/leagues/join")
                        .sessionAttr("userId", USER_ID)
                        .sessionAttr("isAdmin", false)
                        .param("code", "ABCD1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fantasy/leagues/" + joinedLeague.getId()))
                .andExpect(flash().attributeExists("successMessage"));

        verify(fantasyService).joinLeague(eq(USER_ID), any(JoinFantasyLeagueRequestDTO.class));
    }

    @Test
    void loggedUser_whenSavingLineup_shouldStoreSelectedPlayersAndRedirectToFantasy() throws Exception {
        when(fantasyService.saveLineup(eq(USER_ID), any(FantasyLineupRequestDTO.class)))
                .thenReturn(List.of(sampleLineupPlayer()));

        mockMvc.perform(post("/fantasy/lineup")
                        .sessionAttr("userId", USER_ID)
                        .sessionAttr("isAdmin", false)
                        .param("playerIds", "10", "11", "12"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fantasy"))
                .andExpect(flash().attributeExists("successMessage"));

        ArgumentCaptor<FantasyLineupRequestDTO> requestCaptor =
                ArgumentCaptor.forClass(FantasyLineupRequestDTO.class);

        verify(fantasyService).saveLineup(eq(USER_ID), requestCaptor.capture());

        assertEquals(List.of(10L, 11L, 12L), requestCaptor.getValue().getPlayerIds());
    }
    @Test
    void loggedUser_whenOpeningSpecificFantasyLeague_shouldSeeSelectedLeagueDashboard() throws Exception {
        FantasyLeagueDTO league = sampleLeague();
        FantasyLineupPlayerDTO lineupPlayer = sampleLineupPlayer();

        when(fantasyService.getMyLeagues(USER_ID)).thenReturn(List.of(league));
        when(fantasyService.getLeagueForMember(league.getId(), USER_ID)).thenReturn(league);
        when(fantasyService.getLeaderboard(league.getId())).thenReturn(List.of());
        when(fantasyService.getAvailablePlayers()).thenReturn(List.of(sampleAvailablePlayer()));
        when(fantasyService.getLineup(USER_ID)).thenReturn(List.of(lineupPlayer));
        when(fantasyService.getMyScore(USER_ID))
                .thenReturn(new FantasyScoreDTO(USER_ID, "acceptance_user", 25, List.of(lineupPlayer)));

        mockMvc.perform(get("/fantasy/leagues/" + league.getId())
                        .sessionAttr("userId", USER_ID)
                        .sessionAttr("isAdmin", false))
                .andExpect(status().isOk())
                .andExpect(view().name("fantasy"))
                .andExpect(model().attributeExists("selectedLeague"))
                .andExpect(model().attributeExists("leaderboard"))
                .andExpect(model().attributeExists("myScore"));
    }
    @Test
    void loggedUser_whenJoiningFantasyLeagueWithInvalidCode_shouldReturnToFantasyWithError() throws Exception {
        when(fantasyService.joinLeague(eq(USER_ID), any(JoinFantasyLeagueRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Fantasy league not found."));

        mockMvc.perform(post("/fantasy/leagues/join")
                        .sessionAttr("userId", USER_ID)
                        .sessionAttr("isAdmin", false)
                        .param("code", "BADCODE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fantasy"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(fantasyService).joinLeague(eq(USER_ID), any(JoinFantasyLeagueRequestDTO.class));
    }

    private FantasyLeagueDTO sampleLeague() {
        return new FantasyLeagueDTO(
                1L,
                "Liga E6 Fantasy",
                "ABCD1234",
                USER_ID,
                "acceptance_user"
        );
    }

    private FantasyAvailablePlayerDTO sampleAvailablePlayer() {
        return new FantasyAvailablePlayerDTO(
                10L,
                "Acceptance Player",
                9,
                PlayerPosition.FORWARD,
                100L,
                "Acceptance FC",
                ""
        );
    }

    private FantasyLineupPlayerDTO sampleLineupPlayer() {
        return new FantasyLineupPlayerDTO(
                10L,
                "Acceptance Player",
                9,
                PlayerPosition.FORWARD,
                100L,
                "Acceptance FC",
                1,
                25
        );
    }
}