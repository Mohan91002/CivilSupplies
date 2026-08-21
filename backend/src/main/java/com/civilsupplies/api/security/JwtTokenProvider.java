package com.civilsupplies.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    /**
     * The development fallback, mirrored from {@code app.jwt.secret} in application.yml. It is
     * committed to the repository, so anyone can forge tokens signed with it — usable for local
     * work, never for prod.
     */
    static final String INSECURE_DEFAULT_SECRET = "default-dev-secret-that-is-at-least-32-bytes-long!!";

    /** HS256 requires a key of at least the hash output size. */
    private static final int MIN_SECRET_BYTES = 32;

    private static final String PROD_PROFILE = "prod";

    private final SecretKey secretKey;
    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret:" + INSECURE_DEFAULT_SECRET + "}") String secret,
            @Value("${app.jwt.access-token-expiry-ms:1800000}") long accessTokenExpiryMs,
            @Value("${app.jwt.refresh-token-expiry-ms:604800000}") long refreshTokenExpiryMs,
            Environment environment) {
        validateSecret(secret, environment.getActiveProfiles());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiryMs = accessTokenExpiryMs;
        this.refreshTokenExpiryMs = refreshTokenExpiryMs;
    }

    private static void validateSecret(String secret, String[] activeProfiles) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must be set: app.jwt.secret resolved to a blank value.");
        }

        int length = secret.getBytes(StandardCharsets.UTF_8).length;
        if (length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "JWT_SECRET must be at least " + MIN_SECRET_BYTES + " bytes for HS256, but was " + length + ".");
        }

        if (Arrays.asList(activeProfiles).contains(PROD_PROFILE) && INSECURE_DEFAULT_SECRET.equals(secret)) {
            throw new IllegalStateException(
                    "JWT_SECRET must be set under the '" + PROD_PROFILE + "' profile. Refusing to start with the "
                            + "built-in development signing key, which is public in the repository and would let "
                            + "anyone forge admin tokens.");
        }
    }

    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiryMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiryMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getAccessTokenExpiryMs() {
        return accessTokenExpiryMs;
    }
}
