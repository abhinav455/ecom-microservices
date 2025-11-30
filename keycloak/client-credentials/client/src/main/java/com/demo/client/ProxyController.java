package com.demo.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProxyController {

	private final Service2Client client;

	@GetMapping("/proxy")
	public String proxy(){
		return client.fetchData();
	}

}
