package com.searchaword.security.controller;

import com.searchaword.security.entity.User;
import com.searchaword.security.jwt.JwtUtil;
import com.searchaword.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return Map.of("error", "Username already exists");
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                "ROLE_USER"
        );

        userRepository.save(user);

        return Map.of("message", "User registered successfully");
    }

    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return Map.of("token", token);
    }
}