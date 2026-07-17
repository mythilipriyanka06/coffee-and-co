package com.coffeeandco.cafe.config;

import com.coffeeandco.cafe.model.User;
import com.coffeeandco.cafe.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository) {

        return args -> {

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = new User();
                admin.setFullName("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ADMIN");
                userRepository.save(admin);
            }

            if (userRepository.findByEmail("user@gmail.com").isEmpty()) {
                User user = new User();
                user.setFullName("User");
                user.setEmail("user@gmail.com");
                user.setPassword(encoder.encode("user123"));
                user.setRole("CUSTOMER");
                userRepository.save(user);
            }
        };
    }
}