package com.javid.gateway.filter;

import com.javid.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    private static final List<String> OPEN_ENDPOINTS = List.of(
            "/external/users/register",
            "/external/users/confirm-registration",
            "/external/auth/login",
            "/external/auth/refresh-token",
            "/eureka"
    );

    public AuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            if (isSecured(path)) {

                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange, "Authorization header is missing");
                }

                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return onError(exchange, "Invalid Authorization header format");
                }

                String token = authHeader.substring(7);

                if (!jwtUtil.isTokenValid(token)) {
                    return onError(exchange, "JWT Token is expired or invalid");
                }

                try {

                    Claims claims = jwtUtil.extractAllClaims(token);
                    String userId = claims.get("userId", String.class);
                    String email = claims.get("email", String.class);
                    List<String> roles = claims.get("roles", List.class);

                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Email", email)
                            .header("X-User-Roles", String.join(",", roles))
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                } catch (Exception e){
                    log.error("An error occurred when read JWT token: {}", e.getMessage(), e);
                    return onError(exchange, "Invalid JWT Token: " + e.getMessage());
                }
            }
            return chain.filter(exchange);
        };
    }

    private boolean isSecured(String path) {
        boolean secured = OPEN_ENDPOINTS.stream().noneMatch(path::startsWith);
        log.info("Path: {}, isSecured: {}", path, secured);
        return secured;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err) {
        log.error("Authentication error: {}", err);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }


    public static class Config {}
}