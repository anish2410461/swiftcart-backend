package ecommerse.project.service;

import ecommerse.project.dto.CartResponse;
import ecommerse.project.model.CartItem;
import ecommerse.project.model.Product;
import ecommerse.project.repository.CartRepository;
import ecommerse.project.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    // ✅ Add item — validates quantity, merges duplicates
    public CartItem addToCart(String username, CartItem item) {

        // 🛡️ 1. Quantity must be positive
        if (item.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        // 🔍 2. Product must exist
        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

        // 🛡️ 3. Stock check (null-safe — products without stock field default to 0)
        int availableStock = product.getStock() != null ? product.getStock() : 0;
        if (availableStock < item.getQuantity()) {
            throw new RuntimeException("Not enough stock for: " + product.getName()
                    + " (available: " + availableStock + ")");
        }

        // 🔄 4. Merge if already in cart
        List<CartItem> existing = cartRepository.findByUsername(username);
        Optional<CartItem> duplicate = existing.stream()
                .filter(c -> c.getProductId().equals(item.getProductId()))
                .findFirst();

        if (duplicate.isPresent()) {
            CartItem existingItem = duplicate.get();
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
            return cartRepository.save(existingItem);
        }

        item.setUsername(username);
        return cartRepository.save(item);
    }

    // ✅ Get all cart items for a user
    public List<CartItem> getCart(String username) {
        return cartRepository.findByUsername(username);
    }

    // ✅ Cart total using real product prices
    public double getCartTotal(String username) {
        List<CartItem> items = cartRepository.findByUsername(username);

        // ✅ FIX: Return 0 if cart is empty instead of throwing an exception
        if (items.isEmpty()) {
            return 0.0;
        }

        return items.stream().mapToDouble(item -> {
            // ✅ FIX: Skip items where product was deleted from DB
            Optional<Product> product = productRepository.findById(item.getProductId());
            if (product.isEmpty()) return 0.0;
            return product.get().getPrice() * item.getQuantity();
        }).sum();
    }

    // ✅ Get cart as rich DTO (frontend-ready)
    public List<CartResponse> getCartResponse(String username) {
        List<CartItem> items = cartRepository.findByUsername(username);

        // ✅ FIX: Filter out cart items whose product was deleted from DB
        return items.stream()
                .filter(item -> productRepository.existsById(item.getProductId()))
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId()).get();
                    double subtotal = product.getPrice() * item.getQuantity();

                    byte[] image = product.getMainImage();
                    String type = product.getMainImageType();

                    // 🖼️ FALLBACK: If primary image is missing, use the first gallery image
                    if (image == null && product.getAdditionalImages() != null && !product.getAdditionalImages().isEmpty()) {
                        image = product.getAdditionalImages().get(0);
                        type = product.getAdditionalImageTypes().get(0);
                    }

                    return new CartResponse(item.getId(), item.getProductId(),
                            product.getName(), item.getQuantity(), product.getPrice(), subtotal,
                            image, type);
                }).toList();
    }

    // ✅ Safe remove — verifies item exists before deleting
    public void removeItem(String id) {
        if (!cartRepository.existsById(id)) {
            throw new RuntimeException("Cart item not found: " + id);
        }
        cartRepository.deleteById(id);
    }

    // ✅ Update quantity — validates stock before saving
    public CartItem updateQuantity(String id, int quantity) {
        CartItem item = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + id));

        if (quantity <= 0) {
            cartRepository.deleteById(id);
            return null;
        }

        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() != null && product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock");
        }

        item.setQuantity(quantity);
        return cartRepository.save(item);
    }
}
