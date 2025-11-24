package com.ecommerce.gateway;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class FallbackController {

	@GetMapping("/fallback/products")
	public ResponseEntity<List<String>> productsFallback(){
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(Collections.singletonList("Product service is unavailable, please try after sometime"));
	}

}
