package ecommerse.project.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String username;              // matches JWT / Cart pattern
    private List<OrderItem> items;        // snapshot of cart at order time
    private double totalAmount;
    private String status;                // PLACED, SHIPPED, DELIVERED
    private LocalDateTime orderDate = LocalDateTime.now();

    // Snapshot of what was bought — stored independently of the Product document
    @Data
    public static class OrderItem {
        private String productId;
        private String productName;
        private int quantity;
        private double price;
        private String category;          // "Tech" or "Fashion"
        private byte[] mainImage;         // Image snapshot
        private String mainImageType;
    }
}
