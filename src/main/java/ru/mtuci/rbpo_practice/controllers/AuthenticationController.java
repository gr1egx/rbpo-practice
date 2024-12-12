package ru.mtuci.rbpo_practice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.rbpo_practice.configuration.JwtProvider;
import ru.mtuci.rbpo_practice.models.*;
import ru.mtuci.rbpo_practice.repositories.UserRepository;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            String email = request.getEmail();

            ApplicationUser user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found."));

            authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email, request.getPassword())
                    );

            String token = jwtProvider.createToken(email, user.getRole().getAuthorities());

            return ResponseEntity.ok(new AuthResponse(email, token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            String email = request.getEmail();

            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Email address is already in use.");
            }

            BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

            ApplicationUser user = new ApplicationUser();
            user.setEmail(email);
            user.setPassword(pwdEncoder.encode(request.getPassword()));
            user.setUsername(request.getUsername());
            user.setRole(Role.USER);

            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK).body("Successful registration.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong.");
        }
    }
}
