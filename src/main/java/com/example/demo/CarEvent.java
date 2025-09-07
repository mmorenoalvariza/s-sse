package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarEvent {
    private String eventType;
    private Car car;
    private LocalDateTime timestamp;
    private String source;

    public static CarEvent carAdded(Car car) {
        return new CarEvent("CAR_ADDED", car, LocalDateTime.now(), "spring-web-demo");
    }
}