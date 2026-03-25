package com.example.football_manager.controller;

import com.example.football_manager.dto.CountryRequestDTO;
import com.example.football_manager.service.CountryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminCountryViewController {

    private final CountryService countryService;

    public AdminCountryViewController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/admin/countries")
    public String countriesPage(Model model) {
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("countryRequest", new CountryRequestDTO());
        return "admin-countries";
    }

    @PostMapping("/admin/countries")
    public String createCountry(@ModelAttribute CountryRequestDTO countryRequest) {
        countryService.createCountry(countryRequest);
        return "redirect:/admin/countries";
    }

    @PostMapping("/admin/countries/delete/{id}")
    public String deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
        return "redirect:/admin/countries";
    }
}