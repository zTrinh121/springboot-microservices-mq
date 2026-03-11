package com.trinhntm.orderservice.controller;

import com.trinhntm.orderservice.dto.Order;
import com.trinhntm.orderservice.dto.OrderEvent;
import com.trinhntm.orderservice.publisher.OrderEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderEventPublisher orderEventPublisher;

    public  OrderController(OrderEventPublisher orderEventPublisher) {
        this.orderEventPublisher = orderEventPublisher;
    }

    @PostMapping("/orders")
    public String placeOrder(@RequestBody Order order) {
       order.setOrderId(UUID.randomUUID().toString());
       OrderEvent orderEvent = new OrderEvent();
       orderEvent.setStatus("PENDING");
       orderEvent.setMessage("Order is in pending status");
       orderEvent.setOrder(order);

       orderEventPublisher.publishOrderEvent(orderEvent);

        return "Order published to SNS successfully! Order ID: " + order.getOrderId();
    }
}
