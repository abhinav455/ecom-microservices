package com.ecommerce.gateway;


import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {


	@Bean
	public RedisRateLimiter redisRateLimiter(){
		return new RedisRateLimiter(10,20,1);
	}

	@Bean //on which key we want to rate limit like ip/userid
	public KeyResolver hostNameKeyResolver(){
		return exchange -> Mono.just(
				exchange.getRequest().getRemoteAddress().getHostName());  //.getQueryParams().getFirst("user"));
	}


	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder){


		return builder.routes()
				.route("product-service", r-> r
						.path("/api/products/**")
//						.filters(f -> f.rewritePath("/api/products(?<segment>/?.*)",
//								"/api/products${segment}"))
						.filters(f-> f
								.retry(retryConfig -> retryConfig
												.setRetries(10)
												.setMethods(HttpMethod.GET)
												)
								.requestRateLimiter(config -> config
										.setRateLimiter(redisRateLimiter())
										.setKeyResolver(hostNameKeyResolver()))
								.circuitBreaker(config -> config
										.setName("ecomBreaker")
										.setFallbackUri("forward:/fallback/products")))
						.uri("lb://PRODUCT-SERVICE"))

//				 // "**/products/**"  and filter and forward "/(?<segment1>[^/]*)/products(?<segment2>/?.*)"
//				.route("product-service", r-> r
//						.path("/api/products/**")
//						.uri("lb://PRODUCT-SERVICE"))
				.route("user-service", r-> r
						.path("/api/users/**")
						.filters(f -> f.rewritePath("/api/users(?<segment>/?.*)",
								"/api/users${segment}"))
						.uri("lb://USER-SERVICE"))
				.route("order-service", r-> r
						.path("/api/orders/**", "/api/cart/**")
						.filters(f -> f.rewritePath("/api/(?<segment>.*)",
								"/api/${segment}"))
						.uri("lb://ORDER-SERVICE"))
				.route("eureka-server", r-> r
						.path("/eureka/main")
						.filters(f-> f
								.rewritePath("/eureka/main", "/"))
						.uri("http://eureka:8761")) //with docker
						//.uri("http://localhost:8761"))
				.route("eureka-server-static", r-> r
						.path("/eureka/**")
						.uri("http://eureka:8761")) //with docker
				//.uri("http://localhost:8761"))
				.build();

	}

}
