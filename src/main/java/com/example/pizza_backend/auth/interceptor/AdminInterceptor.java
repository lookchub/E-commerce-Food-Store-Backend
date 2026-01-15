package com.example.pizza_backend.auth.interceptor;


import com.example.pizza_backend.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    public static final int ROLE_ADMIN = 2;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        try {
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                return true;
            }
            // 1. find cookie named tokenpizza
            Cookie[] cookies = request.getCookies();
            if (cookies == null || cookies.length == 0) {
                throw new UnauthorizedException("No cookies found");
            }

            String token = null;
            for (Cookie cookie : cookies) {
                if ("tokenpizza".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
            if (token == null) {
                throw new UnauthorizedException("No tokenpizza found");
            }

            // 2. decrypt JWT
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            Long profileId = claims.getBody().get("profile_id", Long.class);
            String username = claims.getBody().get("username", String.class);
            Integer profileRole = claims.getBody().get("profile_role", Integer.class);
            request.setAttribute("profile_id", profileId);
            request.setAttribute("username", username);
            request.setAttribute("profile_role", profileRole);

            // 3. check admin role
            if (profileRole != null && profileRole == ROLE_ADMIN) {
                return true;
            }

            throw new UnauthorizedException("You are not admin");

        } catch (JwtException e) {
            throw new UnauthorizedException("Invalid token");
        }
    }
}
