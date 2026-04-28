package com.example.football_manager.service;

import com.example.football_manager.dto.CountryRequestDTO;
import com.example.football_manager.model.Country;
import com.example.football_manager.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CountryServiceTest {

    private CountryRepository countryRepository;
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        countryRepository = mock(CountryRepository.class);
        countryService = new CountryService(countryRepository);
    }

    @Test
    void createCountry_shouldCreateCountrySuccessfully() {
        CountryRequestDTO dto = new CountryRequestDTO(" Spain ");

        Country savedCountry = new Country();
        savedCountry.setId(1L);
        savedCountry.setName("Spain");

        when(countryRepository.save(any(Country.class))).thenReturn(savedCountry);

        Country result = countryService.createCountry(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Spain", result.getName());

        verify(countryRepository).save(any(Country.class));
    }

    @Test
    void getAllCountries_shouldReturnCountries() {
        Country spain = new Country();
        spain.setId(1L);
        spain.setName("Spain");

        Country france = new Country();
        france.setId(2L);
        france.setName("France");

        when(countryRepository.findAll()).thenReturn(List.of(spain, france));

        List<Country> result = countryService.getAllCountries();

        assertEquals(2, result.size());
        assertEquals("Spain", result.get(0).getName());
        assertEquals("France", result.get(1).getName());

        verify(countryRepository).findAll();
    }

    @Test
    void getAllCountries_shouldReturnEmptyList() {
        when(countryRepository.findAll()).thenReturn(List.of());

        List<Country> result = countryService.getAllCountries();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(countryRepository).findAll();
    }

    @Test
    void deleteCountry_shouldDeleteSuccessfully() {
        Country country = new Country();
        country.setId(1L);
        country.setName("Spain");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));

        countryService.deleteCountry(1L);

        verify(countryRepository).findById(1L);
        verify(countryRepository).delete(country);
    }

    @Test
    void deleteCountry_shouldThrowExceptionWhenCountryNotFound() {
        when(countryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> countryService.deleteCountry(99L)
        );

        assertEquals("Country not found with id: 99", ex.getMessage());

        verify(countryRepository).findById(99L);
        verify(countryRepository, never()).delete(any(Country.class));
    }
}