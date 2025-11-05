package com.example.ordermatcher.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id
    @Column(length = 36)
    private String id;

    private String symbol;
    private long quantity;
    private long remaining;
    private double price;
    @Enumerated(EnumType.STRING)
    private Side side;
    private Instant createdAt;
    private boolean filled;

    public static Order newOrder(String symbol, long qty, double price, Side side) {
        return Order.builder()
                .id(UUID.randomUUID().toString())
                .symbol(symbol)
                .quantity(qty)
                .remaining(qty)
                .price(price)
                .side(side)
                .createdAt(Instant.now())
                .filled(false)
                .build();
    }

    public enum Side { BUY, SELL }
}