package com.ecommerce.notification;


import java.util.Map;
import java.util.function.Consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.ecommerce.notification.payload.OrderStatus;
import com.ecommerce.order.dto.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderEventConsumer {

//	@RabbitListener(queues = "${rabbitmq.queue.name}")
//	public void handleOrderEvent(OrderCreatedEvent orderEvent){//Map<String, Object> orderEvent){
//									//if didnt provide class
//
//		System.out.println("Received Order Event: " + orderEvent);
//
//		Long orderId = orderEvent.getOrderId(); //.get("orderId").toString());
//		OrderStatus orderStatus = orderEvent.getStatus(); //.get("status").toString();
//
//
//		System.out.println("Order ID: " + orderId);
//		System.out.println("Order Status: " + orderStatus);
//
//		//update db
//		//send notification
//		//generate invoice
//		//send seller ship order service
//
//	}

	@Bean
	public Consumer<OrderCreatedEvent>  orderCreated(){
		return event -> {
			System.out.println("Received order created event for order: " +  event.getOrderId());
			log.info("Received order created event for order: {}", event.getOrderId());
			log.info("Received order created event for user id: {}", event.getUserId());
		};
	}






}
