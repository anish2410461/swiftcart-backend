package ecommerse.project.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "cart")
public class CartItem {

    @Id
    private String id;

    private String username;   // 🔥 identifies which user owns this cart item
    private String productId;
    private int quantity;
}
