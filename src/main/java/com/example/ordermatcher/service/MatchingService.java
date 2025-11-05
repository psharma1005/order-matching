package com.example.ordermatcher.service;

import com.example.ordermatcher.dto.OrderRequest;
import com.example.ordermatcher.model.Order;
import com.example.ordermatcher.repo.OrderRepository;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.*;

@Service
public class MatchingService {
    private static final Logger log = LoggerFactory.getLogger(MatchingService.class);

    private final OrderRepository orderRepository;
    private final ExecutorService executor;
    private final ConcurrentMap<String, SymbolOrderBook> books = new ConcurrentHashMap<>();

    public MatchingService(OrderRepository orderRepository,
                           @Value("${matching.thread-pool-size:8}") int threads) {
        this.orderRepository = orderRepository;
        this.executor = Executors.newFixedThreadPool(Math.max(2, threads));
    }

    public CompletableFuture<Order> submit(OrderRequest req) {
        return CompletableFuture.supplyAsync(() -> {
            Order.Side side = Order.Side.valueOf(req.getSide().toUpperCase());
            Order order = Order.newOrder(req.getSymbol(), req.getQuantity(), req.getPrice(), side);
            orderRepository.save(order);
            SymbolOrderBook book = books.computeIfAbsent(order.getSymbol(), SymbolOrderBook::new);
            book.match(order);
            orderRepository.save(order);
            return order;
        }, executor);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

    static class SymbolOrderBook {
        private final String symbol;
        private final Object lock = new Object();
        private final PriorityQueue<Order> buys = new PriorityQueue<>(
                Comparator.<Order>comparingDouble(o -> -o.getPrice()).thenComparing(Order::getCreatedAt)
        );
        private final PriorityQueue<Order> sells = new PriorityQueue<>(
                Comparator.<Order>comparingDouble(Order::getPrice).thenComparing(Order::getCreatedAt)
        );

        SymbolOrderBook(String symbol) { this.symbol = symbol; }

        void match(Order incoming) {
            synchronized (lock) {
                PriorityQueue<Order> opposite = incoming.getSide() == Order.Side.BUY ? sells : buys;
                while (incoming.getRemaining() > 0 && !opposite.isEmpty()) {
                    Order top = opposite.peek();
                    boolean priceCross = incoming.getSide() == Order.Side.BUY ? incoming.getPrice() >= top.getPrice()
                            : incoming.getPrice() <= top.getPrice();
                    if (!priceCross) break;
                    long trade = Math.min(incoming.getRemaining(), top.getRemaining());
                    incoming.setRemaining(incoming.getRemaining() - trade);
                    top.setRemaining(top.getRemaining() - trade);
                    if (top.getRemaining() == 0) {
                        top.setFilled(true);
                        opposite.poll();
                    }
                    if (incoming.getRemaining() == 0) {
                        incoming.setFilled(true);
                    }
                }
                if (!incoming.isFilled()) {
                    if (incoming.getSide() == Order.Side.BUY) buys.add(incoming);
                    else sells.add(incoming);
                }
            }
        }
    }
}