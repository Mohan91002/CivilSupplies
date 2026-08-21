package com.civilsupplies.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "this-is-a-32-byte-long-test-secret-for-jwt-signing-please";
    private static final String OTHER_SECRET = "another-32-byte-long-secret-for-the-jwt-impl-please-x";

    private static final long ACCESS_EXPIRY_MS = 1_800_000L;
    private static final long REFRESH_EXPIRY_MS = 604_800_000L;

    private final JwtTokenProvider provider =
            new JwtTokenProvider(SECRET, ACCESS_EXPIRY_MS, REFRESH_EXPIRY_MS);

    private static Authentication authentication() {
        UserDetails user = User.withUsername("admin@example.com")
                .password("irrelevant")
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                .build();
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Test
    void issuesParseableAccessToken() {
        String token = provider.generateAccessToken(authentication());

        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUsernameFromToken(token)).isEqualTo("admin@example.com");
    }

    @Test
    void accessTokenCarriesRolesClaim() {
        String token = provider.generateAccessToken(authentication());

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("admin@example.com");
        assertThat(claims.get("roles", String.class)).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void issuesParseableRefreshToken() {
        String token = provider.generateRefreshToken("admin@example.com");

        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUsernameFromToken(token)).isEqualTo("admin@example.com");
    }

    @Test
    void rejectsTokenSignedWithDifferentSecret() {
        JwtTokenProvider other = new JwtTokenProvider(OTHER_SECRET, ACCESS_EXPIRY_MS, REFRESH_EXPIRY_MS);
        String foreign = other.generateRefreshToken("x@y.com");

        assertThat(provider.validateToken(foreign)).isFalse();
    }

    @Test
    void rejectsMalformedToken() {
        assertThat(provider.validateToken("not-a-jwt")).isFalse();
    }

    @Test
    void rejectsAlreadyExpiredToken() {
        JwtTokenProvider instantlyExpiring = new JwtTokenProvider(SECRET, -1_000L, -1_000L);
        String expired = instantlyExpiring.generateRefreshToken("admin@example.com");

        assertThat(instantlyExpiring.validateToken(expired)).isFalse();
    }

    @Test
    void exposesConfiguredAccessTokenExpiry() {
        assertThat(provider.getAccessTokenExpiryMs()).isEqualTo(ACCESS_EXPIRY_MS);
    }
}
