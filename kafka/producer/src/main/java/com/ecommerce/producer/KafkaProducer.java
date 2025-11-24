package com.ecommerce.producer;
//
//
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api")
//public class KafkaProducer {
//
//	private final KafkaTemplate<String, RiderLocation> kafkaTemplate;
//
//	public KafkaProducer(KafkaTemplate<String, RiderLocation> kafkaTemplate) {
//		this.kafkaTemplate = kafkaTemplate;
//	}
//
//
//	@PostMapping("/send")
//	public String sendMessage(@RequestParam String message){
//
//		//in rabbit, produce to  exchange with key, exchange does binding to queue
//		   //based on topic exchange etc.
//		   //consumer listen to queue
//
//		//in kafka, just a single topic, both producer and consumer to topic
//		  //but can create many consumer groups, each consumer on partitions and offset
//		  //of those partitions
//
//		RiderLocation location = new RiderLocation("rider123", 28.61, 77.23);
//
//		kafkaTemplate.send("my-topic-new-rider", location);
//		return "Message sent: " + location.toString();
//	}
//
//
//}
