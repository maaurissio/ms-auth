package cl.duoc.cordillera.service;

import cl.duoc.cordillera.dto.LoginRequestDTO;
import cl.duoc.cordillera.dto.LoginResponseDTO;
import cl.duoc.cordillera.dto.RefreshTokenRequestDTO;
import cl.duoc.cordillera.dto.TokenValidationResponseDTO;
import cl.duoc.cordillera.entity.Credencial;
import cl.duoc.cordillera.entity.Token;
import cl.duoc.cordillera.enums.EstadoUsuario;
import cl.duoc.cordillera.enums.TipoToken;
import cl.duoc.cordillera.repository.CredencialRepository;
import cl.duoc.cordillera.repository.TokenRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;

import java.time.LocalDateTime;
import java.util.UUID;


import org.eclipse.microprofile.jwt.JsonWebToken;
import io.smallrye.jwt.auth.principal.JWTParser;

@ApplicationScoped
public class AuthService {

    @Inject
    CredencialRepository credencialRepository;

    @Inject
    TokenRepository tokenRepository;

    @Inject
    JwtService jwtService;

    @Inject
    JWTParser parser;

    @Inject
    JsonWebToken jwt;

    //LOGIN
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {

        Credencial credencial = credencialRepository.buscarPorEmail(request.email)
                .orElseThrow(() -> new NotAuthorizedException("Credenciales invalidas"));

        if (credencial.estado != EstadoUsuario.ACTIVO) {
            throw new NotAuthorizedException("Usuario no activo");
        }

        if (!credencial.validarPassword(request.password)) {
            throw new NotAuthorizedException("Credenciales invalidas");
        }

        // Generar tokens
        String accessToken = jwtService.generarAccessToken(credencial.usuarioId, credencial.email);
        String refreshToken = jwtService.generarRefreshToken(credencial.usuarioId, credencial.email);

        // Revocar refresh tokens anteriores
        tokenRepository.buscarRefreshActivosPorUsuario(credencial.usuarioId)
                .forEach(t -> {
                    t.revocar();
                    tokenRepository.persist(t);
                });

        // Guardar nuevo refresh token
        Token token = new Token();
        token.usuarioId = credencial.usuarioId;
        token.token = refreshToken;
        token.tipo = TipoToken.REFRESH_TOKEN;
        token.expiracion = LocalDateTime.now().plusDays(7);
        token.revocado = false;

        tokenRepository.persist(token);

        return new LoginResponseDTO(
                credencial.usuarioId,
                credencial.email,
                accessToken,
                refreshToken
        );
    }

    //REFRESH TOKEN
    @Transactional
    public LoginResponseDTO refresh(RefreshTokenRequestDTO request) {

        Token token = tokenRepository.buscarPorToken(request.refreshToken)
                .orElseThrow(() -> new NotAuthorizedException("Refresh token invalido"));

        if (token.revocado || token.estaExpirado()) {
            throw new NotAuthorizedException("Refresh token invalido o expirado");
        }

        Credencial credencial = credencialRepository.buscarPorUsuarioId(token.usuarioId)
                .orElseThrow(() -> new NotAuthorizedException("Usuario no encontrado"));

        String newAccessToken = jwtService.generarAccessToken(credencial.usuarioId, credencial.email);

        return new LoginResponseDTO(
                credencial.usuarioId,
                credencial.email,
                newAccessToken,
                request.refreshToken // se mantiene el mismo refresh
        );
    }

    //VALIDAR TOKEN (para BFF)
    public TokenValidationResponseDTO validar() {
        try {
            return new TokenValidationResponseDTO(
                    true,
                    UUID.fromString(jwt.getSubject()),
                    jwt.getClaim("email")
            );
        } catch (Exception e) {
            return new TokenValidationResponseDTO(false, null, null);
        }
    }
    //LOGOUT
    @Transactional
    public void logout(String refreshToken) {

        Token token = tokenRepository.buscarPorToken(refreshToken)
                .orElseThrow(() -> new NotAuthorizedException("Token no encontrado"));

        token.revocar();
        tokenRepository.persist(token);
    }
}