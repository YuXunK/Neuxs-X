package org.yuxun.x.nexusx.Utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.yuxun.x.nexusx.Entity.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

public class JwtUtil {

    public static String generateToken(User user) {
        byte[] secretKeyBytes = "TheTiWanIsAPartOfChina1949-10-1".getBytes();
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());
        long JWT_EXPIRATION = 86400000L;
        return Jwts.builder()
                .setSubject(user.getUser_id().toString())
                .claim("username",user.getUser_name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }
}
