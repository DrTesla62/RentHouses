package com.example.renthouses.service;

import com.example.renthouses.dto.LoginRequest;
import com.example.renthouses.dto.RegisterRequest;
import com.example.renthouses.entity.Role;
import com.example.renthouses.entity.User;
import com.example.renthouses.repository.RoleRepository;
import com.example.renthouses.security.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RoleRepository roleRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.roleRepository = roleRepository;
    }

    public User register(RegisterRequest request) {
        // First, find or create the role
        Role role = roleRepository.findByName(request.getRole())
                .orElseGet(() -> {
                    Role newRole = new Role(request.getRole());
                    return roleRepository.save(newRole);
                });

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);  // Use the persisted role
        return userService.createUser(user);
    }

    public String login(LoginRequest request) {
        logger.debug("Attempting login for user: {}", request.getUsername());

        // Find the user and get their stored hashed password
        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    logger.debug("User not found: {}", request.getUsername());
                    return new BadCredentialsException("Invalid username or password");
                });

        // For debugging - print the provided password (be careful with this in production!)
        logger.debug("Provided raw password: {}", request.getPassword());
        logger.debug("Stored hashed password: {}", user.getPassword());

        // This internally hashes the raw password and compares with stored hash
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            logger.debug("Password doesn't match for user: {}", user.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        // If we get here, password matched
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );

        return jwtTokenProvider.generateToken(authentication);
    }
}