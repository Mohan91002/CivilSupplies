package com.civilsupplies.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String SECRET = "this-is-a-32-byte-long-test-secret-for-jwt-signing-please";
    private static final String OTHER_SECRET = "another-32-byte-long-secret-for-the-jwt-impl-please-x";

    private static final long ACCESS_EXPIRY_MS = 1_800_000L;
    private static final long REFRESH_EXPIRY_MS = 604_800_000L;

    private final JwtTokenProvider provider = provider(SECRET, "dev");

    private static Environment environment(String... activeProfiles) {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles(activeProfiles);
        return environment;
    }

    private static JwtTokenProvider provider(String secret, String... activeProfiles) {
        return new JwtTokenProvider(secret, ACCESS_EXPIRY_MS, REFRESH_EXPIRY_MS, environment(activeProfiles));
    }

    private static Authentication authentication() {
        UserDetails user = User.withUsername("admin@example.com")
                .password("irrelevant")
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                .build();
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    // ---------- token issuing / parsing ----------

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
        String foreign = provider(OTHER_SECRET, "dev").generateRefreshToken("x@y.com");

        assertThat(provider.validateToken(foreign)).isFalse();
    }

    @Test
    void rejectsMalformedToken() {
        assertThat(provider.validateToken("not-a-jwt")).isFalse();
    }

    @Test
    void rejectsAlreadyExpiredToken() {
        JwtTokenProvider instantlyExpiring =
                new JwtTokenProvider(SECRET, -1_000L, -1_000L, environment("dev"));
        String expired = instantlyExpiring.generateRefreshToken("admin@example.com");

        assertThat(instantlyExpiring.validateToken(expired)).isFalse();
    }

    @Test
    void exposesConfiguredAccessTokenExpiry() {
        assertThat(provider.getAccessTokenExpiryMs()).isEqualTo(ACCESS_EXPIRY_MS);
    }

    // ---------- secret validation ----------

    @Test
    void prodProfileWithBuiltInDefaultSecretIsRejected() {
        assertThatThrownBy(() -> provider(JwtTokenProvider.INSECURE_DEFAULT_SECRET, "prod"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET must be set");
    }

    @Test
    void prodProfileAmongSeveralStillRejectsBuiltInDefaultSecret() {
        assertThatThrownBy(() -> provider(JwtTokenProvider.INSECURE_DEFAULT_SECRET, "metrics", "prod"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET must be set");
    }

    @Test
    void prodProfileWithSuppliedSecretStarts() {
        assertThatCode(() -> provider(SECRET, "prod")).doesNotThrowAnyException();
    }

    @Test
    void builtInDefaultSecretRemainsUsableOutsideProd() {
        assertThatCode(() -> provider(JwtTokenProvider.INSECURE_DEFAULT_SECRET, "dev"))
                .doesNotThrowAnyException();
    }

    @Test
    void blankSecretIsRejected() {
        assertThatThrownBy(() -> provider("   ", "dev"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("blank");
    }

    @Test
    void secretShorterThanHs256RequirementIsRejected() {
        assertThatThrownBy(() -> provider("too-short", "dev"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 32 bytes");
    }
}
