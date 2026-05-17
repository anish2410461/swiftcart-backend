package ecommerse.project.controller;

import ecommerse.project.config.JwtUtil;
import ecommerse.project.model.Order;
import ecommerse.project.service.OrderService;
import ecommerse.project.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5175")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, JwtUtil jwtUtil, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
        this.orderRepository = orderRepository;
    }

    // ✅ Place order — converts cart into an order
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestHeader("Authorization") String header) {
        try {
            String username = jwtUtil.extractUsername(header.substring(7));
            Order savedOrder = orderService.placeOrder(username);
            return ResponseEntity.ok(savedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to place order due to a server error.");
        }
    }

    // ✅ Confirm order after successful payment
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmOrder(@RequestHeader("Authorization") String header, @RequestBody Object paymentData) {
        // In a production app, you'd verify paymentData.paymentIntentId with Stripe here
        return placeOrder(header);
    }

    // ✅ View order history
    @GetMapping
    public List<Order> getOrders(@RequestHeader("Authorization") String header) {
        String username = jwtUtil.extractUsername(header.substring(7));
        return orderService.getOrders(username);
    }

    // 1. ADMIN: Get all orders
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    // 3. ADMIN: Update the status of an order
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String id, @RequestParam String status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(status);
            Order updatedOrder = orderRepository.save(order);
            return ResponseEntity.ok(updatedOrder);
        }).orElse(ResponseEntity.notFound().build());
    }
}
