package com.example.ordermatcher.controller;

import com.example.ordermatcher.dto.OrderRequest;
import com.example.ordermatcher.dto.OrderResponse;
import com.example.ordermatcher.model.Order;
import com.example.ordermatcher.repo.OrderRepository;
import com.example.ordermatcher.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final MatchingService matchingService;
    private final OrderRepository repo;

    public OrderController(MatchingService matchingService, OrderRepository repo) {
        this.matchingService = matchingService;
        this.repo = repo;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<OrderResponse>> placeOrder(@RequestBody OrderRequest req) {
        return matchingService.submit(req)
                .thenApply(order -> ResponseEntity.ok(new OrderResponse(order.getId(), order.isFilled() ? "FILLED" : "OPEN",
                        "Remaining: " + order.getRemaining())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}