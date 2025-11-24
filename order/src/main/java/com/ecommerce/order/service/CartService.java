package com.ecommerce.order.service;

import com.ecommerce.order.clients.ProductServiceClient;
import com.ecommerce.order.clients.UserServiceClient;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.model.CartItem;

import com.ecommerce.order.repository.CartItemRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    //private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private  final ProductServiceClient productServiceClient;
    private  final UserServiceClient userServiceClient;
	int attempt = 0;

   // private final UserRepository userRepository;


	//@CircuitBreaker(name="productService", fallbackMethod = "addToCartFallBack")
	@Retry(name="retryBreaker", fallbackMethod = "addToCartFallBack")
	public boolean addToCart(String userId, CartItemRequest request) {

		System.out.println("ATTEMPT COUNT: " + ++attempt);

        //validation product exists or not
        //check stock quantity
        //get user
//
        ProductResponse productResponse =  productServiceClient.getProductDetails(request.getProductId());
                   //productRepository.findById(request.getProductId());
//        if(productOpt.isEmpty()){
//            return false;
//        }

        if(productResponse == null || productResponse.getStockQuantity() < request.getQuantity()){
            return false;
        }

 //       ProductResponse product = productOpt.get();


        UserResponse userResponse =  userServiceClient.getUserDetails(userId);
        if(userResponse == null ){
            return false;
        }


//
//        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
//        if(userOpt.isEmpty()){
//            return false;
//        }
//
//        User user = userOpt.get();

        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, String.valueOf(request.getProductId()));
        if(existingCartItem != null){
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(productResponse.getStockQuantity()));//product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
        }else {

            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(BigDecimal.valueOf(productResponse.getStockQuantity())); //product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItemRepository.save(cartItem);
        }

        return true;
    }


	public boolean addToCartFallBack(String userId, CartItemRequest request, Exception exception){
		System.out.println("FALLBACK CALLED");
		exception.printStackTrace();
		return false;
	}





    @Transactional
    public boolean deleteItemFromCart(String userId, String productId) {

//        Optional<Product> productOpt = productRepository.findById(productId);
//        if(productOpt.isEmpty()){
//            return false;
//        }
//
//        Product product = productOpt.get();
//
//        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
//        if(userOpt.isEmpty()){
//            return false;
//        }
//
//        User user = userOpt.get();

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if(cartItem!=null){
            cartItemRepository.delete(cartItem);
            return true;
        }

        return false;

    }

    public List<CartItem> getCart(String userId) {

        return   cartItemRepository.findByUserId(userId);    //userRepository.findById(Long.valueOf(userId))
               // .map(cartItemRepository::findByUser).orElseGet(List::of);

    }

    @Transactional
    public void clearCart(String userId) {

        cartItemRepository.deleteByUserId(userId);
       // userRepository.findById(Long.valueOf(userId))
        //        .ifPresent(cartItemRepository::deleteByUser);

    }
}
