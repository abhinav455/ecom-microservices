package com.ecommerce.order.service;



import com.ecommerce.order.dto.OrderCreatedEvent;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.dto.OrderItemDTO;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.*;
import com.ecommerce.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {


    private final CartService cartService;
    private final OrderRepository orderRepository;
//	private final RabbitTemplate rabbitTemplate;

//	@Value("${rabbitmq.exchange.name}")
//	private String exchangeName;
//
//	@Value("${rabbitmq.routing.key}")
//	private String routingKey;


	private final StreamBridge streamBridge;



    public Optional<OrderResponse> createOrder(String userId) {

        //validate for cart items
        List<CartItem> cartItems = cartService.getCart(userId);
        if(cartItems.isEmpty()){
            return Optional.empty();
        }

        //validate for user
//        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
//        if(userOptional.isEmpty()){
//            return Optional.empty();
//        }
//
//        User user = userOptional.get();

        //calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);



        //create order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        null,
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                ))
                .toList();

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
         //orderItems also saved


        //clear the cart
        cartService.clearCart(userId);

	    OrderCreatedEvent event = new OrderCreatedEvent(
				savedOrder.getId(),
			    savedOrder.getUserId(),
			    savedOrder.getStatus(),
			    mapToOrderItemDTOs(savedOrder.getItems()),
			    savedOrder.getTotalAmount(),
			    savedOrder.getCreatedAt()
	    );

//		rabbitTemplate.convertAndSend(exchangeName,
//				routingKey,
////				Map.of("orderId", savedOrder.getId(),   //key1, val1
////						"status", "CREATED")            //key2, val2
//				event);

	    streamBridge.send("createOrder-out-0", event);

        return Optional.of(mapToOrderResponse(savedOrder));

    }



	private List<OrderItemDTO> mapToOrderItemDTOs(List<OrderItem> items){

		return items
				.stream()
				.map(item -> new OrderItemDTO(
						item.getId(),
						item.getProductId(),
						item.getQuantity(),
						item.getPrice(),
						item.getPrice().multiply(new BigDecimal(item.getQuantity()))
				)).toList();

	}



    private OrderResponse mapToOrderResponse(Order order) {
         return new OrderResponse(
                 order.getId(),
                 order.getTotalAmount(),
                 order.getStatus(),
		         mapToOrderItemDTOs(order.getItems()),
//                 order.getItems().stream()
//                         .map(item -> new OrderItemDTO(
//                                 item.getId(),
//                                 item.getProductId(),
//                                 item.getQuantity(),
//                                 item.getPrice(),
//                                 item.getPrice().multiply(new BigDecimal(item.getQuantity()))
//                                 ))
//                         .toList(),
                 order.getCreatedAt()
         );
    }

}
