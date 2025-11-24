//package com.ecommerce.consumer;
//
//
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class KafkaConsumer {
//
//
////	@KafkaListener(topics = "my-topic-new", groupId = "my-new-group")
////	public void listen(String message){
////
////		System.out.println("Received Message: " + message);
////	}
////
////	@KafkaListener(topics = "my-topic", groupId = "my-new-group")
////	public void listen2(String message){
////
////		System.out.println("Received Message: " + message);
////	}
//
//
//	@KafkaListener(topics = "my-topic-new-rider", groupId = "my-new-group-rider")
//	public void listenRiderLocation(RiderLocation location){
//
//		System.out.println("Received Message: " +location.getRiderId()
//			+ " : " + location.getLatitude() + " : " + location.getLongitude());
//
//	}
//
//
//}
