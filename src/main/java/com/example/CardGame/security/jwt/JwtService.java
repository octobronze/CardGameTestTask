package com.example.CardGame.security.jwt;

import com.example.CardGame.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtService {
    private static final String JWT_EXPIRED = "Jwt has been expired";
    private static final String JWT_NOT_VALID = "Jwt not valid";

    @Value("${jwt.signing.key}")
    private String userTokenSigningKey;

    @Value("${jwt.duration}")
    private Long jwtDuration;

    public JwtCheckResponse checkUserToken(String token) {
        return checkToken(token, userTokenSigningKey);
    }

    private JwtCheckResponse checkToken(String token, String signingKey) {
        try {
            getClaims(token, signingKey);
        } catch (ExpiredJwtException expiredJwtException) {
            return new JwtCheckResponse(false, JWT_EXPIRED);
        } catch (MalformedJwtException | SignatureException signatureException) {
            return new JwtCheckResponse(false, JWT_NOT_VALID);
        }

        return new JwtCheckResponse(true, "");
    }

    private Claims getClaims(String token, String signingKey) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8)))
                .build().parseSignedClaims(token).getPayload();
    }

    public Integer getUserId(String token) {
        Claims claims = getClaims(token, userTokenSigningKey);

        return Integer.parseInt(claims.getSubject());
    }

    public String generateTokenByUserPrincipal(UserPrincipal userPrincipal) {
        return generateToken(
                new HashMap<>(),
                String.valueOf(userPrincipal.getId()),
                Keys.hmacShaKeyFor(userTokenSigningKey.getBytes(StandardCharsets.UTF_8)));
    }

    private String generateToken(Map<String, Object> claims, String subject, Key signingKey) {

        return Jwts.builder().subject(subject).claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtDuration))
                .signWith(signingKey).compact();
    }
}
