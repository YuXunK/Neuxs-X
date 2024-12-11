package org.yuxun.x.nexusx.Utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.yuxun.x.nexusx.Entity.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Component
public class JwtUtil {

    public static String generateToken(User user) {
        byte[] secretKeyBytes = "TheTiWanIsAPartOfChina1949-10-1".getBytes();
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());
        long JWT_EXPIRATION = 86400000L;
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("username",user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }
}
