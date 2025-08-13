package com.example.mysite.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private  String secret;
    @Value("${jwt.expiration}")
    private  long expiration;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String CreatJwt(UserDetails userDetails,int id){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id",id)
                .withClaim("name",userDetails.getUsername())
                .withExpiresAt(GetExpireTime())
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    private Date GetExpireTime() {
        return new Date(System.currentTimeMillis()+expiration*1000);
    }

    public boolean validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        System.out.println("decodedJWT = " + decodedJWT);
        return !decodedJWT.getExpiresAt().before(new Date());
    }

    public UserDetails getUserFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            System.out.println("decodedJWT = " + decodedJWT);
                Map<String, Claim> claims = decodedJWT.getClaims();
                return User.withUsername(claims.get("name").asString())
                        .password("******")
                        .build();
        }catch (TokenExpiredException e){
            return null;
        }
    }

    public boolean invalidateJWT(String token){
        Algorithm algorithm=Algorithm.HMAC256(secret);
        JWTVerifier verifier=JWT.require(algorithm).build();
        try{
            DecodedJWT decodedJWT=verifier.verify(token);
            String id=decodedJWT.getId();
            return deleteJWT(id,decodedJWT.getExpiresAt());
        }catch (TokenExpiredException e){
            return false;
        }
    }

    private boolean deleteJWT(String id, Date expiresAt) {
        if (isInvalidJwt(id)){
            return false;
        }
        Date now=new  Date();
        long expire=Math.max(0, expiresAt.getTime()-now.getTime());
        stringRedisTemplate.opsForValue().set(Const.JWT_BLACK_LIST+id,"",expire, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean isInvalidJwt(String id) {
        return stringRedisTemplate.hasKey(Const.JWT_BLACK_LIST+id);
    }


}
