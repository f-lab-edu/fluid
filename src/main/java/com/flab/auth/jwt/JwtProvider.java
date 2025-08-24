package com.flab.auth.jwt;

import com.flab.auth.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-seconds}")
    private long expirationSeconds;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * AccessToken을 생성하는 메서드입니다.
     * @param user
     * @return
     */
    public String generateAccessToken(User user){

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationSeconds * 1000);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("fluid/role", user.getRole())
                .setIssuedAt(now) //발급 시각
                .setExpiration(expiry) // 만료시각
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
