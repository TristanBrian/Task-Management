package com.example.taskmanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int LIMIT = 100;   // requests per minute
    private static final Duration WINDOW = Duration.ofMinutes(1);

    @Autowired(required = false) // if Redis is not available, don't fail
    private RedisTemplate<String, Long> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // If Redis is not configured, skip rate limiting entirely.
        if (redisTemplate == null) {
            logger.warn("❌ RedisTemplate not available – rate limiting disabled");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Get real client IP from X-Forwarded-For header
            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                clientIp = request.getRemoteAddr();
            } else {
                clientIp = clientIp.split(",")[0].trim();
            }

            String key = "rate:limiter:" + clientIp;

            // Atomic increment – creates key with 1 if it doesn't exist
            Long count = redisTemplate.opsForValue().increment(key);
            // Set TTL only on first request
            if (count == 1) {
                redisTemplate.expire(key, WINDOW);
            }

            logger.info("Rate limit count for IP " + clientIp + ": " + count);

            if (count > LIMIT) {
                response.setStatus(429);
                response.getWriter().write("Too many requests – rate limit exceeded");
                return;
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // If Redis fails (connection, timeout, etc.), allow the request and log the error.
            logger.error("❌ Redis error in rate limiter: " + e.getMessage(), e);
            // Fallback: allow the request without rate limiting
            filterChain.doFilter(request, response);
        }
    }

    // Skip rate limiting for public endpoints (registration, login, Swagger)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/swagger-ui.html");
    }
}