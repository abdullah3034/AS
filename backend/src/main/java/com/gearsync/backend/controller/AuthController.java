package com.gearsync.backend.controller;

import com.gearsync.backend.dto.LoginRequest;
import com.gearsync.backend.dto.UserDto;
import com.gearsync.backend.dto.VehicleDto;
import com.gearsync.backend.dto.SignupRequest;
import com.gearsync.backend.model.Vehicle;
import com.gearsync.backend.model.User;
import com.gearsync.backend.security.JwtUtil;
import com.gearsync.backend.service.AuthService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (authService.isEmailRegistered(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }
        User saved = authService.register(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (authService.isEmailRegistered(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }
        User savedUser = authService.signupWithVehicle(request);
        return ResponseEntity.ok(Map.of("id", savedUser.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    boolean isAuthenticated = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
    if (!isAuthenticated) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    User user = authService.findByEmail(loginRequest.getEmail()); // 👈 fetch user
    String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

    return ResponseEntity.ok(Map.of("token", jwtToken, "role", user.getRole().name()));
}

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        String email = authentication.getName();
        User user = authService.findByEmail(email);
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setPhone(user.getPhone());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(Authentication authentication) {
        String email = authentication.getName();
        User user = authService.findByEmail(email);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Stateless JWT: usually handled client-side; placeholder for future blacklist impl
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
}