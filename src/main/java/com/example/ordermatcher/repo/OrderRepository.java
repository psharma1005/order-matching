package com.example.ordermatcher.repo;

import com.example.ordermatcher.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findBySymbolAndSideOrderByPriceDescCreatedAtAsc(String symbol, Order.Side side);
    List<Order> findBySymbolOrderByPriceDescCreatedAtAsc(String symbol);
}