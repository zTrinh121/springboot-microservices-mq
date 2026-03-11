package com.trinhntm.orderservice.publisher;

import com.trinhntm.orderservice.dto.OrderEvent;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final SnsTemplate snsTemplate;

    @Value("${sns.topic.name}")
    private String topicName;

    public void publishOrderEvent(OrderEvent orderEvent) {
        log.info("Publishing order event to SNS topic: {}", topicName);

        try {
            // SnsTemplate có method sendNotification đơn giản
            snsTemplate.sendNotification(topicName, orderEvent, orderEvent.getStatus());

            log.info("Successfully published order event: {}", orderEvent.getOrder().getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish order event", e);
            throw new RuntimeException("Failed to publish order event", e);
        }
    }
}