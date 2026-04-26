package cl.duoc.cordillera.repository;

import cl.duoc.cordillera.entity.Credencial;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CredencialRepository implements PanacheRepositoryBase<Credencial, UUID> {

    public Optional<Credencial> buscarPorEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<Credencial> buscarPorUsuarioId(UUID usuarioId) {
        return find("usuarioId", usuarioId).firstResultOptional();
    }
}