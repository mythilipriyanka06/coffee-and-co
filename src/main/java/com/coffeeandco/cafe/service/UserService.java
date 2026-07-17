package com.coffeeandco.cafe.service;

import com.coffeeandco.cafe.model.User;
import com.coffeeandco.cafe.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }
        // Set default role if null
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CUSTOMER");
        }
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Login with BCrypt support.
     * If the stored password is a BCrypt hash, uses BCrypt matching.
     * If the stored password is plain text (legacy), compares directly
     * and then auto-upgrades it to BCrypt on successful login.
     */
    public User login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        String storedPassword = user.getPassword();

        // Check if stored password is a BCrypt hash
        if (storedPassword != null && storedPassword.startsWith("$2")) {
            // BCrypt comparison
            if (passwordEncoder.matches(password, storedPassword)) {
                return user;
            }
        } else {
            // Plain-text comparison (legacy users) — auto-upgrade to BCrypt
            if (storedPassword != null && storedPassword.equals(password)) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                return user;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
