package com.surgegate.backend.controllers;

import com.surgegate.backend.dto.*;
import com.surgegate.backend.entities.User;
import com.surgegate.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
        if(userRepository.findByEmail(user.getEmail()).isPresent())
            return ResponseEntity.badRequest().body("Email exists");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ATTENDEE"); // Default role
        userRepository.save(user);
        return ResponseEntity.ok("User Registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();
        if (passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(token, user.getRole()));
        }
        return ResponseEntity.status(401).body("Invalid");
    }
}