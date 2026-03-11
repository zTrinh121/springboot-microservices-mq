package com.trinhntm.emailservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trinhntm.emailservice.dto.OrderEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailSqsConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${sqs.queue.email}")
    private String queueName;

    @SqsListener("${sqs.queue.email}")
    public void receiveStockMessage(String message) {
        log.info("========== EMAIL SERVICE RECEIVED ==========");
        log.info("Raw message: {}", message);
        try {
            if (message.contains("\"Message\":")) {
                var snsMessage = objectMapper.readTree(message);
                String actualMessage = snsMessage.get("Message").asText();

                OrderEvent orderEvent = objectMapper.readValue(actualMessage, OrderEvent.class);

                log.info("Order ID: {}", orderEvent.getOrder().getOrderId());
                log.info("Product: {}", orderEvent.getOrder().getName());
                log.info("Quantity: {}", orderEvent.getOrder().getQuantity());

                // Process stock logic
            }
        } catch(Exception e){
            log.error("Error processing stock message", e);
        }
    }
}
