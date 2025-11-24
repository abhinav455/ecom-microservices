package com.ecommerce.gateway;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@Component
public class LoggingFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        logger.info("Incoming request to: {}" , exchange.getRequest().getPath());
        return chain.filter(exchange);
        //Pass the request (exchange) to the next filter in the chain.

        // return chain.filter(exchange)
        //            .then(Mono.fromRunnable(() -> {
        //                logger.info("After response from backend");
        //            }));
    }
}
