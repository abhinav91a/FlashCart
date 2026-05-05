package com.flashcart.auth.app;

import com.flashcart.auth.api.AuthRequest;
import com.flashcart.auth.api.AuthResponse;
import com.flashcart.auth.config.JwtService;
import com.flashcart.auth.domain.User;
import com.flashcart.auth.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwtService) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(AuthRequest req) {
        if (repo.findByEmail(req.email()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User user = User.builder()
                .email(req.email())
                .password(encoder.encode(req.password()))
                .role("USER")
                .build();
        repo.save(user);
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getEmail(), user.getRole());
    }

    public AuthResponse login(AuthRequest req) {
        User user = repo.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!encoder.matches(req.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getEmail(), user.getRole());
    }
}