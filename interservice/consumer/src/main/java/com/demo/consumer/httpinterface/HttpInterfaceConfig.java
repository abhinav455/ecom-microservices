package com.demo.consumer.httpinterface;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;


@Configuration
public class HttpInterfaceConfig {

//    @Bean     //already bean defined in webclient package
//    @LoadBalanced
//    public WebClient.Builder webClient(){
//        return WebClient.builder();
//
//    }

    @Bean
    public ProviderHttpInterface webClientHttpInterface(WebClient.Builder webClientBuilder){

        WebClient webClient = webClientBuilder
                .baseUrl("http://provider")
                .build();

        //adapter pattern and proxy pattern for webClient

        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        //factory pattern for our custom ProviderHttpInterface endpoints
        ProviderHttpInterface service = factory.createClient(ProviderHttpInterface.class);

        return service;

    }

}
