package ecommerse.project.controller;

import ecommerse.project.dto.LoginRequest;
import ecommerse.project.dto.RegisterRequest;
import ecommerse.project.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {

        authService.register(request);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String identifier = request.getEmail() != null ? request.getEmail() : request.getUsername();
            String token = authService.login(identifier, request.getPassword());
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}

