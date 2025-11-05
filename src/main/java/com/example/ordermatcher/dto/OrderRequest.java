package com.example.ordermatcher.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderRequest {
    private String symbol;
    private long quantity;
    private double price;
    private String side;
}