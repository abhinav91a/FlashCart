package com.flashcart.auth.api;

public record AuthResponse(String token, String email, String role) {}