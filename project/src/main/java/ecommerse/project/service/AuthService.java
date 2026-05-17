package ecommerse.project.service;

import ecommerse.project.config.JwtUtil;
import ecommerse.project.dto.RegisterRequest;
import ecommerse.project.model.User;
import ecommerse.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public void register(RegisterRequest request) {
        System.out.println("REGISTER API CALLED");
        System.out.println("Saving user: " + request.getUsername());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // HASH PASSWORD
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole("USER");
        System.out.println("Saving to DB...");
        userRepository.save(user);
        System.out.println("Saved to DB successfully!");
    }

    public String login(String identifier, String password) {

        User user = userRepository.findByEmail(identifier);
        
        if (user == null) {
            user = userRepository.findByUsername(identifier);
        }

        if (user == null) {
            throw new RuntimeException("User not found: " + identifier);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // GENERATE TOKEN (includes role claim)
        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }
}
