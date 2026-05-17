package ecommerse.project.controller;

import ecommerse.project.config.JwtUtil;
import ecommerse.project.dto.CartResponse;
import ecommerse.project.model.CartItem;
import ecommerse.project.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5175")
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final JwtUtil jwtUtil;

    public CartController(CartService cartService, JwtUtil jwtUtil) {
        this.cartService = cartService;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Add item to cart — validates qty, merges duplicates
    @PostMapping
    public CartItem addToCart(@RequestBody CartItem item,
                              @RequestHeader("Authorization") String header) {
        String username = jwtUtil.extractUsername(header.substring(7));
        return cartService.addToCart(username, item);
    }

    // ✅ View raw cart items
    @GetMapping
    public List<CartItem> getCart(@RequestHeader("Authorization") String header) {
        String username = jwtUtil.extractUsername(header.substring(7));
        return cartService.getCart(username);
    }

    // ✅ View cart with product names & prices (frontend-ready)
    @GetMapping("/detail")
    public List<CartResponse> getCartDetail(@RequestHeader("Authorization") String header) {
        String username = jwtUtil.extractUsername(header.substring(7));
        return cartService.getCartResponse(username);
    }

    // ✅ Get cart grand total
    @GetMapping("/total")
    public double getCartTotal(@RequestHeader("Authorization") String header) {
        String username = jwtUtil.extractUsername(header.substring(7));
        return cartService.getCartTotal(username);
    }

    // ✅ Remove item — safe, throws 404 if missing
    @DeleteMapping("/{id}")
    public String removeItem(@PathVariable String id) {
        cartService.removeItem(id);
        return "Item removed from cart";
    }

    // ✅ Update quantity endpoint
    @PutMapping("/{id}")
    public CartItem updateQuantity(@PathVariable String id, @RequestParam int quantity) {
        return cartService.updateQuantity(id, quantity);
    }
}
