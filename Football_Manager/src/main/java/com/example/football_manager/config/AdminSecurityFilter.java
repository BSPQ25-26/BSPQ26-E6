package com.example.football_manager.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AdminSecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();

        boolean isProtectedPath = uri.startsWith("/admin") ||
                uri.startsWith("/teams/add") ||
                uri.startsWith("/teams/edit") ||
                uri.startsWith("/teams/delete");

        if (isProtectedPath) {
            HttpSession session = req.getSession(false);

            Boolean isAdmin = (session != null) ? (Boolean) session.getAttribute("isAdmin") : null;

            if (isAdmin == null || !isAdmin) {
                res.sendRedirect("/teams");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}