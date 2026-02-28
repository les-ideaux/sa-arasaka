package fr.lesideaux.saarasaka.backend.controller;

import fr.lesideaux.saarasaka.backend.config.JwtService;
import fr.lesideaux.saarasaka.backend.data.entity.UserEntity;
import fr.lesideaux.saarasaka.backend.data.repository.UserRepository;
import fr.lesideaux.saarasaka.backend.dto.AuthRequest;
import fr.lesideaux.saarasaka.backend.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(401).body("Email ou mot de passe incorrect");
        }

        String token = jwtService.generateToken(user.getId(), user.getRole().name(), user.getEmail());

        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getId(),
                user.getRole().name(),
                user.getFirstName(),
                user.getLastName()
        ));
    }
}
