package com.example.football_manager.controller;

import com.example.football_manager.dto.CountryRequestDTO;
import com.example.football_manager.model.Country;
import com.example.football_manager.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(CountryControllerTest.class);

    @Mock
    private CountryService countryService;

    private CountryController countryController;

    @BeforeEach
    void setUp() {
        countryController = new CountryController(countryService);
    }

    @Test
    void createCountry_shouldReturnCreatedCountry() {
        CountryRequestDTO dto = new CountryRequestDTO("Spain");
        Country country = new Country(1L, "Spain");

        logger.info("Create country test: name={}", dto.getName());

        when(countryService.createCountry(dto)).thenReturn(country);

        ResponseEntity<Country> response = countryController.createCountry(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(country, response.getBody());
        verify(countryService).createCountry(dto);
    }

    @Test
    void getAllCountries_shouldReturnCountries() {
        Country country = new Country(1L, "Spain");

        logger.info("Get all countries test: expectedCount={}", 1);

        when(countryService.getAllCountries()).thenReturn(List.of(country));

        ResponseEntity<List<Country>> response = countryController.getAllCountries();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(country), response.getBody());
        verify(countryService).getAllCountries();
    }

    @Test
    void deleteCountry_shouldReturnNoContent() {
        logger.info("Delete country test: id={}", 1L);
        ResponseEntity<Void> response = countryController.deleteCountry(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(countryService).deleteCountry(1L);
    }
}