package com.sureprompt.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails alice;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="
        );
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 60_000L);
        ReflectionTestUtils.setField(jwtService, "jwtRefreshExpiration", 120_000L);

        alice = User.withUsername("alice")
                .password("pw")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void generateTokenExtractUsernameAndValidate() {
        String token = jwtService.generateToken(alice);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
        assertThat(jwtService.isTokenValid(token, alice)).isTrue();
    }

    @Test
    void tokenForOneUserIsInvalidForAnotherUser() {
        String token = jwtService.generateToken(alice);

        UserDetails bob = User.withUsername("bob")
                .password("pw")
                .authorities("ROLE_USER")
                .build();

        assertThat(jwtService.isTokenValid(token, bob)).isFalse();
    }
}
