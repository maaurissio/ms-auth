package cl.duoc.cordillera.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration; 
import java.util.UUID;

@ApplicationScoped
public class JwtService {

    private static final String ISSUER = "grupo-cordillera-auth";

    public String generarAccessToken(UUID usuarioId, String email) {
    return Jwt.issuer(ISSUER)
            .subject(usuarioId.toString())
            .claim("usuarioId", usuarioId.toString())
            .claim("email", email)
            .claim("jti", UUID.randomUUID().toString()) // 🔥 clave
            .expiresIn(Duration.ofMinutes(15))
            .sign();
    }

    public String generarRefreshToken(UUID usuarioId, String email) {
    return Jwt.issuer("grupo-cordillera-auth")
            .subject(usuarioId.toString())
            .claim("usuarioId", usuarioId.toString())
            .claim("email", email)
            .claim("tipo", "REFRESH")
            .claim("jti", UUID.randomUUID().toString()) // 🔥 clave
            .expiresIn(Duration.ofDays(7))
            .sign();
    }

}