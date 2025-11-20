package com.rogan.webnotify.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class JWTservice {

    private String secretkey="";
    public JWTservice(){
        try {
            KeyGenerator keygen=KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keygen.generateKey();
            secretkey=Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }


    public String generateToken(String username) {
        Map<String,Object>claims=new HashMap<>();
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1*60*60*1000))
                .and()
                .signWith(getkey())
                .compact();

    }

    private SecretKey getkey() {
        byte[] key= Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(key);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser().verifyWith(getkey())
                .build()
                .parseSignedClaims(token).getPayload();
        return claimsResolver.apply(claims);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String extractedUsername = extractUserName(token);
        return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    // Extract expiration
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
