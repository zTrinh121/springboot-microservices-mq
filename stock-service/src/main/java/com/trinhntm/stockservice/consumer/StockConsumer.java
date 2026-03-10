package com.trinhntm.stockservice.consumer;

import com.trinhntm.stockservice.dto.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class StockConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(StockConsumer.class);

    @RabbitListener(queues = "${rabbitmq.queue.stock.name}")
    public void consumeMessage(OrderEvent orderEvent) {
        LOGGER.info("Received Order Event -> {}", orderEvent.toString());

        // handle logic or save order event data to database
    }
}
