package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CarEventService {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public CarEventService(SqsTemplate sqsTemplate, 
                          ObjectMapper objectMapper,
                          @Value("${aws.sqs.car-events-queue-url:}") String queueUrl) {
        this.sqsTemplate = sqsTemplate;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }

    public void publishCarAddedEvent(Car car) {
        if (queueUrl == null || queueUrl.isEmpty()) {
            log.warn("SQS queue URL not configured, skipping event publishing");
            return;
        }

        try {
            CarEvent event = CarEvent.carAdded(car);
            String eventJson = objectMapper.writeValueAsString(event);
            
            sqsTemplate.send(queueUrl, eventJson);
            log.info("Published car added event for car ID: {} to queue: {}", car.getId(), queueUrl);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize car event for car ID: {}", car.getId(), e);
        } catch (Exception e) {
            log.error("Failed to publish car event for car ID: {}", car.getId(), e);
        }
    }

    @SqsListener("${aws.sqs.car-events-queue-url:}")
    public void receiveCarEvent(String message) {
        log.info("Received message from car-events-queue: {}", message);
        
        try {
            CarEvent event = objectMapper.readValue(message, CarEvent.class);
            log.info("Parsed CarEvent - Type: {}, Car: {}", event.getEventType(), event.getCar());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse car event message: {}", message, e);
        } catch (Exception e) {
            log.error("Error processing car event message: {}", message, e);
        }
    }
}