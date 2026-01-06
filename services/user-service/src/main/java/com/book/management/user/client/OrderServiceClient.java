package com.book.management.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

// "order-service" should match the spring.application.name of your order service
@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @DeleteMapping("/deleteByUser/{userId}")
    ResponseEntity<Void> deleteOrdersByUserId(@PathVariable("userId") Long userId);
}