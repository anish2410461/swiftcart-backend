package ecommerse.project.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String name;
    private String description;
    private double price;
    private Integer stock = 0;
    private String category;

    // Main image stored as raw bytes in MongoDB
    private byte[] mainImage;
    private String mainImageType;

    // Additional gallery images
    private List<byte[]> additionalImages;
    private List<String> additionalImageTypes;
}
