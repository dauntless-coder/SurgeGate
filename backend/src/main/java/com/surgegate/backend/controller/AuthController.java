package com.surgegate.backend.controller;

import com.surgegate.backend.config.JwtUtil;
import com.surgegate.backend.model.User;
import com.surgegate.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ATTENDEE"); // Default role
        userRepository.save(user);
        return ResponseEntity.ok("User Registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        User user = userRepository.findByEmail(creds.get("email")).orElseThrow();
        if (passwordEncoder.matches(creds.get("password"), user.getPassword())) {
            String token = jwtUtil.generateToken(user);
            return ResponseEntity.ok(Map.of("token", token, "role", user.getRole()));
        }
        return ResponseEntity.status(401).body("Invalid Credentials");
    }
}
