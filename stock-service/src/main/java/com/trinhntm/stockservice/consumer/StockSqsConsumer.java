package com.trinhntm.stockservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trinhntm.stockservice.dto.OrderEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockSqsConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${sqs.queue.stock}")
    private String queueName;

    @SqsListener("${sqs.queue.stock}")
    public void receiveStockMessage(String message) {
        log.info("========== STOCK SERVICE RECEIVED ==========");
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

