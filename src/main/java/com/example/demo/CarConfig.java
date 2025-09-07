package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CarConfig {

    @Bean
    public List<Car> carList() {
        return Arrays.asList(
            new Car(1, "BMW"),
            new Car(2, "AUDI"),
            new Car(3, "MERCEDES"),
            new Car(4, "TOYOTA"),
            new Car(5, "HONDA")
        );
    }
}