package cl.duoc.cordillera.repository;

import cl.duoc.cordillera.entity.Token;
import cl.duoc.cordillera.enums.TipoToken;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TokenRepository implements PanacheRepositoryBase<Token, UUID> {

    public Optional<Token> buscarPorToken(String token) {
        return find("token", token).firstResultOptional();
    }

    public List<Token> buscarTokensActivosPorUsuario(UUID usuarioId) {
        return list("usuarioId = ?1 and revocado = false", usuarioId);
    }

    public List<Token> buscarRefreshActivosPorUsuario(UUID usuarioId) {
        return list("usuarioId = ?1 and tipo = ?2 and revocado = false",
                usuarioId,
                TipoToken.REFRESH_TOKEN);
    }
}