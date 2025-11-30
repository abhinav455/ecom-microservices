package com.ecommerce.gateway;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
//		http
//				.authorizeHttpRequests(auth -> auth
//					//.anyRequest()
//						.requestMatchers(("/api/**"))
//					.authenticated())
//				.oauth2ResourceServer(oauth2 -> oauth2
//						.jwt(Customizer.withDefaults()));
//
//		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
//		return http.build();

		return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
				    //jwt token has that roles in it or not
				.authorizeExchange(exchange -> exchange
								.pathMatchers("/api/products/**").hasRole("PRODUCT")
								.pathMatchers("/api/users/**").hasRole("USER")
								.pathMatchers("/api/orders/**").hasRole("ORDER")
								.pathMatchers("/api/cart/**").hasRole("ORDER")
								.anyExchange().authenticated())

					//if jwt token valid
				.oauth2ResourceServer(oauth2 -> oauth2
						//.jwt(Customizer.withDefaults()))
						.jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())))
				.build();


	}


//	@Bean
//	public CorsConfigurationSource corsConfigurationSource(){
//		CorsConfiguration configuration = new CorsConfiguration();
//		configuration.setAllowedOrigins(List.of("http://localhost:5173/"));
//		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
//		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//		configuration.setAllowCredentials(true);
//
//		//create cors object that will map url patterns to this object
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/api/**", configuration);
//		return source;
//
//	}

	//converter to extract roles from jwt, and based on that jwt
	private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor(){
/*
Your method returns a ReactiveJwtAuthenticationConverter, which implements:
Converter<Jwt, Mono<AbstractAuthenticationToken>>
 */

		ReactiveJwtAuthenticationConverter jwtAuthenticationConverter =
				new ReactiveJwtAuthenticationConverter();

		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
			List<String> roles = jwt.getClaimAsMap("resource_access")
					.entrySet().stream()
					.filter(entry ->
							entry.getKey().equals("oauth2-pkce"))
					.flatMap(entry ->
							((Map<String, List<String>>) entry.getValue())
							.get("roles")
							.stream())
					.toList();

			System.out.println("Extracted Roles: " + roles);

			return Flux.fromIterable(roles)
					.map(role -> new SimpleGrantedAuthority("ROLE_" + role));

			//convert String to SimpleGrantedAuthority which represents role type in spring security
		});

		return jwtAuthenticationConverter;

	}


}
