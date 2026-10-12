package com.paymesh.controller;

import com.paymesh.dto.*;
import com.paymesh.model.*;
import com.paymesh.repository.UserRepository;
import com.paymesh.security.JwtService;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt){
        this.users=users; this.encoder=encoder; this.jwt=jwt;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req){
        if(users.existsByEmail(req.email()))
            return ResponseEntity.badRequest().body(Map.of("message","Email already registered"));
        Role role = "MERCHANT".equalsIgnoreCase(req.role()) ? Role.MERCHANT : Role.CUSTOMER;
        User u = new User(req.name(), req.email(), encoder.encode(req.password()), role);
        users.save(u);
        return ResponseEntity.ok(Map.of("message","Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req){
        return users.findByEmail(req.email())
            .filter(u -> encoder.matches(req.password(),u.getPassword()))
            .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of(
                "token", jwt.generateToken(u.getEmail(),u.getRole().name()),
                "name",u.getName(),
                "role",u.getRole().name(),
                "email",u.getEmail()
            )))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message","Invalid email or password")));
    }
}
