
package com.growtrade.gateway.filter;

import com.growtrade.gateway.security.JwtService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }



    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();
        if (exchange.getRequest().getMethod()
                == org.springframework.http.HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }
        // Public APIs
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // Protected APIs
        if (!isProtectedPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty() || !jwtService.isTokenValid(token)) {
            return unauthorized(exchange);
        }

        return chain.filter(exchange);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/api/auth/register")
                || path.equals("/api/auth/login")
                || path.startsWith("/api/stocks/")
                || path.equals("/api/stocks")
                || path.startsWith("/actuator/");
    }

    private boolean matchesBaseOrChild(String path, String basePath) {
        return path.equals(basePath)
                || path.startsWith(basePath + "/");
    }

    private boolean isProtectedPath(String path) {
        return matchesBaseOrChild(path, "/api/users")
                || matchesBaseOrChild(path, "/api/orders")
                || matchesBaseOrChild(path, "/api/portfolio")
                || matchesBaseOrChild(path, "/api/wallets");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
