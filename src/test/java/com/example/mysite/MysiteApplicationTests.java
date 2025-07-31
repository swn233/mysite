package com.example.mysite;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MysiteApplicationTests {
    private static final String SECRET = "your-strong-secret-key";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1小时
    public  long expiration=3600;
    @Test
    void createValidJwt() {
        // 生成一个1小时后过期的JWT
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + EXPIRATION_TIME);

        String token = JWT.create()
                .withClaim("id", 1)
                .withClaim("name", "test")
                .withExpiresAt(expiresAt)
                .withIssuedAt(now)
                .sign(algorithm);

        assertNotNull(token);
        System.out.println("Generated JWT: " + token);
    }

    @Test
    void validateValidJwt() {
        // 生成有效JWT
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        Date expiresAt = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        String token = JWT.create()
                .withClaim("id", 1)
                .withClaim("name", "test")
                .withExpiresAt(expiresAt)
                .sign(algorithm);

        // 验证JWT
        JWTVerifier verifier = JWT.require(algorithm).build();
        var decodedJWT = verifier.verify(token);

        // 断言验证结果
        assertEquals(1, decodedJWT.getClaim("id").asInt());
        assertEquals("test", decodedJWT.getClaim("name").asString());
        assertTrue(decodedJWT.getExpiresAt().after(new Date()));
    }

    @Test
    void rejectExpiredJwt() {
        // 生成已过期的JWT
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        Date expiresAt = new Date(System.currentTimeMillis() - 1000); // 1秒前过期

        String token = JWT.create()
                .withExpiresAt(expiresAt)
                .sign(algorithm);

        // 验证应该抛出异常
        assertThrows(JWTVerificationException.class, () -> {
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
        });
    }

    @Test
    void rejectInvalidSignature() {
        // 使用不同密钥生成JWT
        Algorithm validAlgorithm = Algorithm.HMAC256(SECRET);
        Algorithm invalidAlgorithm = Algorithm.HMAC256("wrong-secret");

        String token = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(validAlgorithm);

        // 使用错误密钥验证
        assertThrows(JWTVerificationException.class, () -> {
            JWTVerifier verifier = JWT.require(invalidAlgorithm).build();
            verifier.verify(token);
        });
    }
    @Test
    void getExpireTime() {
        System.out.println(new Date(System.currentTimeMillis()+expiration*1000));
    }
}
