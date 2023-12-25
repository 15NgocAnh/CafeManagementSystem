package com.mycompany.CafeManagementSystem.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    private String SECRET_KEY = "secret";

    // Phương thức để lấy tên người dùng từ token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Phương thức để lấy thời gian hết hạn của token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Phương thức chung để lấy thông tin từ token bằng cách sử dụng một Resolver
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Phương thức để giải mã các thông tin từ token
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    // Phương thức kiểm tra xem token có hết hạn hay không
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Phương thức để tạo ra một token mới dựa trên tên người dùng và vai trò (role)
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    // Phương thức để tạo ra một token mới dựa trên các thông tin được cung cấp
    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET_KEY).compact();

    }

    // Phương thức kiểm tra tính hợp lệ của token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}


