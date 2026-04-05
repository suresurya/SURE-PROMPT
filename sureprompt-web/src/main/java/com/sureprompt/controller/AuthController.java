package com.sureprompt.controller;

import com.sureprompt.dto.AuthResponse;
import com.sureprompt.dto.LoginRequest;
import com.sureprompt.dto.RefreshTokenRequest;
import com.sureprompt.dto.UserProfileDto;
import com.sureprompt.security.JwtService;
import com.sureprompt.service.UserService;
import com.sureprompt.service.RefreshTokenService;
import com.sureprompt.repository.UserRepository;
import com.sureprompt.entity.User;
import com.sureprompt.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
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
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("REST request to login : {}", request.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            log.error("Authentication failed for user: {}. Error: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(401).build();
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtService.generateToken(userDetails);
        final String refreshToken = jwtService.generateRefreshToken(userDetails);

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        LocalDateTime expiry = LocalDateTime.now().plusDays(7); // matching 604800000ms
        refreshTokenService.createRefreshToken(user.getId(), refreshToken, expiry);

        log.info("User {} logged in successfully", userDetails.getUsername());
        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        String refreshTokenStr = request.getRefreshToken();
        if (refreshTokenStr == null || refreshTokenStr.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        try {
            String username = jwtService.extractUsername(refreshTokenStr);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(refreshTokenStr, userDetails)) {
                
                Optional<RefreshToken> tokenOpt = refreshTokenService.findByToken(refreshTokenStr);
                if (tokenOpt.isEmpty()) {
                    return ResponseEntity.status(401).build();
                }

                RefreshToken storedToken = tokenOpt.get();
                if (storedToken.isRevoked()) {
                    refreshTokenService.revokeTokensForUser(storedToken.getUser().getId());
                    return ResponseEntity.status(401).build();
                }

                if (storedToken.getReplacedByToken() != null) {
                    if (storedToken.getUpdatedAt().plusSeconds(60).isBefore(LocalDateTime.now())) {
                        storedToken.setRevoked(true);
                        refreshTokenService.revokeToken(storedToken);
                        refreshTokenService.revokeTokensForUser(storedToken.getUser().getId());
                        return ResponseEntity.status(401).build();
                    } else {
                        // In grace window
                        String newAccessToken = jwtService.generateToken(userDetails);
                        return ResponseEntity.ok(AuthResponse.builder()
                                .token(newAccessToken)
                                .refreshToken(storedToken.getReplacedByToken())
                                .username(userDetails.getUsername())
                                .build());
                    }
                }

                String newAccessToken = jwtService.generateToken(userDetails);
                String newRefreshToken = jwtService.generateRefreshToken(userDetails);
                
                refreshTokenService.rotateToken(storedToken, newRefreshToken);
                refreshTokenService.createRefreshToken(storedToken.getUser().getId(), newRefreshToken, LocalDateTime.now().plusDays(7));
                
                return ResponseEntity.ok(AuthResponse.builder()
                        .token(newAccessToken)
                        .refreshToken(newRefreshToken)
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
