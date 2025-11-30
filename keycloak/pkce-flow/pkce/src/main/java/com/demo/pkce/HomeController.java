package com.demo.pkce;


//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/api/home")
	public String home(@AuthenticationPrincipal Jwt jwt){
					//@ injects jwt info
				//OAuth2AuthenticationToken token ){
		  //Imp - not using above as we are not oauthclient, but resource server here
		  //Postman is client
//		String email = token.getPrincipal().getAttribute("email");
//		String name = token.getPrincipal().getAttribute("name");
//		String roles = token.getAuthorities().toString();
		String username = jwt.getClaim("preferred_username");



		return "Welcome, " + username;//name + ", " + email + ", " + roles;
	}


}
