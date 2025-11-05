package com.example.ordermatcher.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private String orderId;
    private String status;
    private String message;
}