package com.trinhntm.orderservice.controller;

import com.trinhntm.orderservice.dto.Order;
import com.trinhntm.orderservice.dto.OrderEvent;
import com.trinhntm.orderservice.publisher.OrderProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderProducer orderProducer;
    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping("/orders")
    public String placeOrder(@RequestBody Order order) {
       order.setOrderId(UUID.randomUUID().toString());
       OrderEvent orderEvent = new OrderEvent();
       orderEvent.setStatus("PENDING");
       orderEvent.setMessage("Order is in pending status");
       orderEvent.setOrder(order);

       orderProducer.sendMessage(orderEvent);

       return "Order sent to RabbitMQ ...";
    }
}
