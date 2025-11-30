package com.demo.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Service2Client {

	private final RestTemplate rest;
	private final OAuth2AuthorizedClientManager manager;

	@Value("${service2.url}")
	String service2Url;


	public String fetchData(){

//		var authRequest = OAuth2AuthorizeRequest.withClientRegistrationId("keycloak-client")
//				.principal("machine")
//				.build();
//
//		var client = manager.authorize(authRequest); //gives token
//
//		String token = client.getAccessToken().getTokenValue();
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.setBearerAuth(token);
//
//		var resp = rest.exchange(service2Url + "/data",
//				HttpMethod.GET,
//				new HttpEntity<>(headers),
//				String.class
//		);
//
//		return resp.getBody();

		///below code to forward the token instead of using same access token

		//get token from current authentication object
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		String incomingToken = null;

		if(auth instanceof JwtAuthenticationToken jwtAuthenticationToken){
			incomingToken = jwtAuthenticationToken.getToken().getTokenValue();
		}


		System.out.println("TOKEN VALUE:" + incomingToken);
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(incomingToken);

		var resp = rest.exchange(service2Url + "/data",
				HttpMethod.GET,
				new HttpEntity<>(headers),
				String.class
		);

		return resp.getBody();


	}



}
