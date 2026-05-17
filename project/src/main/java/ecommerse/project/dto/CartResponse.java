package ecommerse.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartResponse {

    private String id;
    private String productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double subtotal;       // unitPrice * quantity

    // Image data — Spring Boot auto-serialises byte[] to Base64 in JSON
    private byte[] mainImage;
    private String mainImageType;
}
