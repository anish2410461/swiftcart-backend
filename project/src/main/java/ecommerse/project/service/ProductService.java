package ecommerse.project.service;

import ecommerse.project.model.Product;
import ecommerse.project.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Add product
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Delete product
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    // Get product by ID
    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    // Update product
    public Product updateProduct(String id, Product updatedProduct) {
        System.out.println("Updating product ID: " + id);
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setStock(updatedProduct.getStock());
            existingProduct.setMainImage(updatedProduct.getMainImage());
            existingProduct.setMainImageType(updatedProduct.getMainImageType());
            existingProduct.setAdditionalImages(updatedProduct.getAdditionalImages());
            existingProduct.setAdditionalImageTypes(updatedProduct.getAdditionalImageTypes());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    // Get products by category
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    // ✅ Unified Search: "Smart Bridge Filter" ensures accuracy across all departments
    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) return new ArrayList<>();
        
        String lowerQuery = query.toLowerCase().trim();
        List<Product> allProducts = productRepository.findAll();
        
        return allProducts.stream()
                .filter(p -> (p.getName() != null && p.getName().toLowerCase().contains(lowerQuery)) || 
                             (p.getDescription() != null && p.getDescription().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
    }
}
