package ecommerse.project.controller;

import ecommerse.project.config.JwtUtil;
import ecommerse.project.model.User;
import ecommerse.project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5175")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestHeader("Authorization") String header) {
        String token = header.substring(7);
        String username = jwtUtil.extractUsername(token);
        return ResponseEntity.ok(userService.getProfile(username));
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestHeader("Authorization") String header, @RequestBody User user) {
        String token = header.substring(7);
        String username = jwtUtil.extractUsername(token);
        return ResponseEntity.ok(userService.updateProfile(username, user));
    }
}
