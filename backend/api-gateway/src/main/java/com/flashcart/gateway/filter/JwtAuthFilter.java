package com.flashcart.gateway.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth",
            "/ws"
    );

    private static final List<String> PUBLIC_GET_PATHS = List.of(
            "/products"
    );

    private static final List<String> ADMIN_ONLY_PATHS = List.of(
            "/inventory"
    );

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // Allow fully public paths
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        // Allow GET on products publicly
        if (method == HttpMethod.GET &&
                PUBLIC_GET_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        // Check Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String role = claims.get("role", String.class);

            // Check admin-only paths
            if (ADMIN_ONLY_PATHS.stream().anyMatch(path::startsWith)) {
                if (!"ADMIN".equals(role)) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            }

            // Check admin-only methods on products
            if (PUBLIC_GET_PATHS.stream().anyMatch(path::startsWith)) {
                if (method == HttpMethod.POST ||
                        method == HttpMethod.PUT ||
                        method == HttpMethod.DELETE) {
                    if (!"ADMIN".equals(role)) {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                }
            }

            // Add user info to headers for downstream services
            ServerWebExchange mutated = exchange.mutate()
                    .request(r -> r
                            .header("X-User-Email", claims.getSubject())
                            .header("X-User-Role", role)
                    )
                    .build();

            return chain.filter(mutated);

        } catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}