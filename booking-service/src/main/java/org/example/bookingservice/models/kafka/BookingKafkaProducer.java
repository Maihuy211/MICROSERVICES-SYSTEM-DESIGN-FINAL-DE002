package org.example.bookingservice.models.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendBookingCreatedEvent(String customerEmail) {
        try {
            kafkaTemplate.send("booking-created", customerEmail);
            log.info("Sent booking-created event for customer email: {}", customerEmail);
        } catch (Exception e) {
            log.error("Failed to send booking-created event for customer email: {}", customerEmail, e);
            throw e;
        }
    }
}
