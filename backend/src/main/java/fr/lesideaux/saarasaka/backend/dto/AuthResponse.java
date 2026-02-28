package fr.lesideaux.saarasaka.backend.dto;

public record AuthResponse(String token, Long userId, String role, String firstName, String lastName) {}
