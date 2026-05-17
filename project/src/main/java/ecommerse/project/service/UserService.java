package ecommerse.project.service;

import ecommerse.project.model.User;
import ecommerse.project.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User updateProfile(String username, User updatedUser) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }

        user.setFullName(updatedUser.getFullName());
        user.setStreetAddress(updatedUser.getStreetAddress());
        user.setCity(updatedUser.getCity());
        user.setState(updatedUser.getState());
        user.setZipCode(updatedUser.getZipCode());

        return userRepository.save(user);
    }

    public User getProfile(String username) {
        return userRepository.findByUsername(username);
    }
}
