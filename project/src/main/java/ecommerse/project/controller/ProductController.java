package ecommerse.project.controller;

import ecommerse.project.model.Product;
import ecommerse.project.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5175")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ✅ Add product with image upload (Admin) — stores image bytes directly in
    // MongoDB
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam(value = "stock", required = false, defaultValue = "0") int stock,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "additionalImages", required = false) List<MultipartFile> additionalImages) {
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStock(stock);
            product.setCategory(category);

            // Store main image bytes directly — no Cloudinary needed
            if (image != null && !image.isEmpty()) {
                product.setMainImage(image.getBytes());
                product.setMainImageType(image.getContentType());
            }

            // Store additional images bytes
            if (additionalImages != null) {
                List<byte[]> imageBytesList = new ArrayList<>();
                List<String> imageTypesList = new ArrayList<>();
                for (MultipartFile file : additionalImages) {
                    if (!file.isEmpty()) {
                        imageBytesList.add(file.getBytes());
                        imageTypesList.add(file.getContentType());
                    }
                }
                product.setAdditionalImages(imageBytesList);
                product.setAdditionalImageTypes(imageTypesList);
            }

            return ResponseEntity.ok(productService.addProduct(product));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to add product: " + e.getMessage());
        }
    }

    // ✅ Get all products (Public)
    @GetMapping
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    // ✅ Get product by ID (Public)
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    // ✅ Update product with optional image re-upload (Admin)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable String id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam(value = "stock", required = false, defaultValue = "0") int stock,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "additionalImages", required = false) List<MultipartFile> additionalImages) {
        try {
            Product existing = productService.getProductById(id);
            existing.setName(name);
            existing.setDescription(description);
            existing.setPrice(price);
            existing.setStock(stock);
            existing.setCategory(category);

            // Only replace image if a new one was provided
            if (image != null && !image.isEmpty()) {
                existing.setMainImage(image.getBytes());
                existing.setMainImageType(image.getContentType());
            }

            // Handle additional images
            if (additionalImages != null) {
                List<byte[]> imageBytesList = new ArrayList<>();
                List<String> imageTypesList = new ArrayList<>();
                for (MultipartFile file : additionalImages) {
                    if (!file.isEmpty()) {
                        imageBytesList.add(file.getBytes());
                        imageTypesList.add(file.getContentType());
                    }
                }
                existing.setAdditionalImages(imageBytesList);
                existing.setAdditionalImageTypes(imageTypesList);
            }

            return ResponseEntity.ok(productService.updateProduct(id, existing));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to update product: " + e.getMessage());
        }
    }

    // ✅ Get products by category (Public)
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    // ✅ Unified Search: Single endpoint for "Tech" and "Fashion"
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam("q") String query) {
        return ResponseEntity.ok(productService.searchProducts(query));
    }

    // ✅ Delete product (Admin)
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return "Product deleted successfully";
    }
}
