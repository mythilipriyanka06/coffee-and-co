package com.coffeeandco.cafe.config;

import com.coffeeandco.cafe.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // Paths requiring Admin role
        if (uri.startsWith("/admin")) {
            if (user == null) {
                response.sendRedirect("/login?error=Please login first.");
                return false;
            }
            if (!"ADMIN".equals(user.getRole())) {
                response.sendRedirect("/shop?error=Access denied. Admin role required.");
                return false;
            }
        }

        // Paths requiring Customer role (or Admin)
        if (uri.startsWith("/shop") || uri.startsWith("/cart") || uri.startsWith("/orders") || uri.startsWith("/invoice") || uri.startsWith("/checkout")) {
            if (user == null) {
                response.sendRedirect("/login?error=Please login to view this page.");
                return false;
            }
        }

        return true;
    }
}
