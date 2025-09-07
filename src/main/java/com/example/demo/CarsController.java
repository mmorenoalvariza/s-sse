package com.example.demo;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarsController {

    private final List<Car> carList;
    private final CarEventService carEventService;

    public CarsController(List<Car> carList, CarEventService carEventService) {
        this.carList = new ArrayList<>(carList);
        this.carEventService = carEventService;
    }

    @GetMapping
    public List<Car> cars() {
        return carList;
    }

    @GetMapping("/{id}")
    public Car car(@PathVariable int id) {
        return carList.stream()
                .filter(car -> car.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @PostMapping
    public Car addCar(@RequestBody Car car) {
        carList.add(car);
        carEventService.publishCarAddedEvent(car);
        return car;
    }
}