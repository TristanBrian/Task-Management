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
import java.time.Instant;
import java.util.concurrent.TimeUnit;

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

        // Use Redis atomic increment + expire
        Integer count = redisTemplate.opsForValue().get(key);
        if (count == null) {
            // First request – set to 1 with TTL
            redisTemplate.opsForValue().set(key, 1, WINDOW);
            count = 1;
        } else {
            // Increment atomically and get new value
            Long newCount = redisTemplate.opsForValue().increment(key);
            count = newCount.intValue();
            // Ensure TTL is set (in case it expired but the key still exists)
            redisTemplate.expire(key, WINDOW);
        }

        if (count > LIMIT) {
            response.setStatus(429);
            response.getWriter().write("Too many requests – rate limit exceeded");
            return;
        }

        filterChain.doFilter(request, response);
    }
}