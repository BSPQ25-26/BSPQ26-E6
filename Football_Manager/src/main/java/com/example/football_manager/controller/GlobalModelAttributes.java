package com.example.football_manager.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void addIsAdmin(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Boolean isAdmin = session != null ? (Boolean) session.getAttribute("isAdmin") : null;
        model.addAttribute("isAdmin", isAdmin != null && isAdmin);
    }
}

