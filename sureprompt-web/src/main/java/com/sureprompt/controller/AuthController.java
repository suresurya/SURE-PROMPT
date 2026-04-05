package com.sureprompt.controller;

import com.sureprompt.dto.AuthResponse;
import com.sureprompt.dto.LoginRequest;
import com.sureprompt.dto.RefreshTokenRequest;
import com.sureprompt.dto.UserProfileDto;
import com.sureprompt.security.JwtService;
import com.sureprompt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtService.generateToken(userDetails);
        final String refreshToken = jwtService.generateRefreshToken(userDetails);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        try {
            String username = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String newAccessToken = jwtService.generateToken(userDetails);
                
                return ResponseEntity.ok(AuthResponse.builder()
                        .token(newAccessToken)
                        .refreshToken(refreshToken)
                        .username(userDetails.getUsername())
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMe(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        String username = authentication.getName();
        // currentUserId can be extracted from principal if needed, for now use username
        UserProfileDto profile = userService.getUserProfile(username, null); 
        return ResponseEntity.ok(profile);
    }
}
