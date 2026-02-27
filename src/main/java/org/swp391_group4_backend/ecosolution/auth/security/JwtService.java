package org.swp391_group4_backend.ecosolution.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
  private final SecretKey key;
  private final long expirationSeconds;

  public JwtService(@Value("${app.jwt.secret}") String secret,
                    @Value("${app.jwt.expiration-seconds}") long expirationSeconds) {
    if (secret == null || secret.length() < 32) {
      throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
    }
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationSeconds = expirationSeconds;
  }

  public String generateAccessToken(User user, UserAuth userAuth) {
    Instant now = Instant.now();
    Instant expiry = now.plusSeconds(expirationSeconds);

    return Jwts.builder()
        .setSubject(user.getId().toString())
        .claim("username", userAuth.getUsername())
        .claim("email", user.getEmail())
        .claim("role", user.getRole().name())
        .claim("status", user.getStatus().name())
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(expiry))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public UUID parseUserId(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();

    return UUID.fromString(claims.getSubject());
  }

  public boolean isTokenValid(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  public long getExpirationSeconds() {
    return expirationSeconds;
  }
}

