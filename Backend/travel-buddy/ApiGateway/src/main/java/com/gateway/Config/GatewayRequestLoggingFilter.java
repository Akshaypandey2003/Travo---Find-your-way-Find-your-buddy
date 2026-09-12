package com.gateway.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class GatewayRequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(GatewayRequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (!path.startsWith("/api/v1/user") && !path.startsWith("/fallback/user-service")) {
            return chain.filter(exchange);
        }

        long startedAt = System.currentTimeMillis();
        boolean hasAuthorization = request.getHeaders().getFirst("Authorization") != null;

        logger.info("Gateway request started: method={}, path={}, authPresent={}, remote={}",
                request.getMethod(), path, hasAuthorization, request.getRemoteAddress());

        return chain.filter(exchange)
                .doOnSuccess(ignored -> logger.info(
                        "Gateway request completed: method={}, path={}, status={}, durationMs={}",
                        request.getMethod(), path, exchange.getResponse().getStatusCode(),
                        System.currentTimeMillis() - startedAt))
                .doOnError(error -> logger.error(
                        "Gateway request failed: method={}, path={}, status={}, durationMs={}, errorType={}, message={}",
                        request.getMethod(), path, exchange.getResponse().getStatusCode(),
                        System.currentTimeMillis() - startedAt, error.getClass().getSimpleName(),
                        error.getMessage(), error));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
