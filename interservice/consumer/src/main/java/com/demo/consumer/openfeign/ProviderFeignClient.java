package com.demo.consumer.openfeign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

//@FeignClient(name="provider-service", url="http://localhost:8081")
@FeignClient(name="provider")  //service discovery+load balanced automatically

public interface ProviderFeignClient {

    @GetMapping("/instance-info")
    String getInstanceInfo();

}