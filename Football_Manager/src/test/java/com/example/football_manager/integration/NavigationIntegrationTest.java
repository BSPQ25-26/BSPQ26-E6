package com.example.football_manager.integration;

import com.example.football_manager.dto.FantasyScoreDTO;
import com.example.football_manager.service.FantasyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:navigationintegrationtest;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc(addFilters = false)
class NavigationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FantasyService fantasyService;

    @Test
    void fantasyPage_withoutSession_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/fantasy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }

    @Test
    void fantasyPage_withSession_shouldLoadSuccessfully() throws Exception {
        Long userId = 1L;

        when(fantasyService.getMyLeagues(userId)).thenReturn(List.of());
        when(fantasyService.getLineup(userId)).thenReturn(List.of());
        when(fantasyService.getAvailablePlayers()).thenReturn(List.of());
        when(fantasyService.getMyScore(userId))
                .thenReturn(new FantasyScoreDTO(userId, "integration_user", 0, List.of()));

        mockMvc.perform(get("/fantasy")
                        .sessionAttr("userId", userId)
                        .sessionAttr("isAdmin", false))
                .andExpect(status().isOk())
                .andExpect(view().name("fantasy"))
                .andExpect(model().attributeExists("myLeagues"))
                .andExpect(model().attributeExists("availablePlayers"))
                .andExpect(model().attributeExists("lineup"))
                .andExpect(model().attributeExists("myScore"));
    }

    @Test
    void fantasyLeaguePage_withoutSession_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/fantasy/leagues/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }
}