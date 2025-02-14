package com.example.renthouses.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.renthouses.repository.UserRepository;
import com.example.renthouses.repository.RoleRepository;
import com.example.renthouses.entity.User;
import com.example.renthouses.entity.Role;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Initialize roles if they don't exist
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setId(3L);
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);
        }

        // Create admin user if it doesn't exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123")); // Change this password
            adminUser.setRole(roleRepository.findByName("ADMIN").get());
            userRepository.save(adminUser);
        }
    }
}