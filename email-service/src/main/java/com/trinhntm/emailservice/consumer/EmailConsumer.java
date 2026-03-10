package com.trinhntm.emailservice.consumer;

import com.trinhntm.emailservice.dto.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(EmailConsumer.class);

    @RabbitListener(queues = "${rabbitmq.queue.email.name}")
    public void consumeMessage(OrderEvent orderEvent) {
        LOGGER.info("Received Order Event -> {}", orderEvent.toString());

        // handle logic or save order event data to database
    }
}
