package com.trinhntm.orderservice.publisher;

import com.trinhntm.orderservice.dto.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.binding.stock.routing.key}")
    private String routingStockKey;

    @Value("${rabbitmq.binding.email.routing.key}")
    private String routingEmailKey;

    private final Logger LOGGER = LoggerFactory.getLogger(OrderProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(OrderEvent orderEvent) {
        LOGGER.info("Order sent to RabbitMQ -> {}", orderEvent.toString());
        // send an order event to stock queue
        rabbitTemplate.convertAndSend(exchangeName, routingStockKey, orderEvent);
        // send an order event to email queue
        rabbitTemplate.convertAndSend(exchangeName, routingEmailKey, orderEvent);
    }
}
