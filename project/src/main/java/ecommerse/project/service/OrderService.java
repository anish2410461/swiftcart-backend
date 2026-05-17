package ecommerse.project.service;

import ecommerse.project.model.CartItem;
import ecommerse.project.model.Order;
import ecommerse.project.model.Product;
import ecommerse.project.model.User;
import ecommerse.project.repository.CartRepository;
import ecommerse.project.repository.OrderRepository;
import ecommerse.project.repository.ProductRepository;
import ecommerse.project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        EmailService emailService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // ✅ Place order — validates stock, reduces stock, snapshots prices
    @Transactional
    public Order placeOrder(String username) {

        List<CartItem> cartItems = cartRepository.findByUsername(username);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty — add items before placing order");
        }

        List<Order.OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem item : cartItems) {

            // 🔍 Fetch real product
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            // 🛡️ Check stock availability
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for: " + product.getName()
                        + " (available: " + product.getStock() + ")");
            }

            // 📉 Reduce stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            // 💰 Real price calculation
            double lineTotal = product.getPrice() * item.getQuantity();
            total += lineTotal;

            // 📸 Snapshot the item at purchase time (safe even if product is later edited/deleted)
            Order.OrderItem snapshot = new Order.OrderItem();
            snapshot.setProductId(item.getProductId());
            snapshot.setProductName(product.getName());
            snapshot.setQuantity(item.getQuantity());
            snapshot.setPrice(product.getPrice());
            snapshot.setCategory(product.getCategory());
            snapshot.setMainImage(product.getMainImage());
            snapshot.setMainImageType(product.getMainImageType());
            orderItems.add(snapshot);
        }

        Order order = new Order();
        order.setUsername(username);
        order.setItems(orderItems);
        order.setTotalAmount(total);
        order.setStatus("PLACED");

        // Save order then clear cart
        Order savedOrder = orderRepository.save(order);
        cartRepository.deleteAll(cartItems);

        // Send Email Confirmation asynchronously
        try {
            User user = userRepository.findByUsername(username);
            String targetEmail = (user != null && user.getEmail() != null) ? user.getEmail() : username;
            
            // In a real prod environment, use @Async or a message queue here
            emailService.sendOrderConfirmation(targetEmail, savedOrder.getId(), total);
        } catch (Exception e) {
            System.err.println("Non-blocking error during email dispatch: " + e.getMessage());
        }

        return savedOrder;
    }

    // ✅ Get all orders for a user
    public List<Order> getOrders(String username) {
        return orderRepository.findByUsername(username);
    }
}
