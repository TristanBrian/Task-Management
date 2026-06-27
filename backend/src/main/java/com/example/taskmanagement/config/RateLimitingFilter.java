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

    private static final int LIMIT = 100;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        String key = "rate:limiter:" + clientIp;

        Integer count = redisTemplate.opsForValue().get(key);
        if (count == null) {
            redisTemplate.opsForValue().set(key, 1, WINDOW);
            count = 1;
        } else {
            Long newCount = redisTemplate.opsForValue().increment(key);
            count = newCount.intValue();
            redisTemplate.expire(key, WINDOW);
        }

        if (count > LIMIT) {
            response.setStatus(429);
            response.getWriter().write("Too many requests – rate limit exceeded");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // ✅ NEW: Skip rate limiting for public endpoints
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/swagger-ui.html");
    }
}