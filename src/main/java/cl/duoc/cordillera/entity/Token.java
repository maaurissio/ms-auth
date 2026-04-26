package cl.duoc.cordillera.entity;

import cl.duoc.cordillera.enums.TipoToken;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens")
public class Token extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "usuario_id", nullable = false)
    public UUID usuarioId;

    @Column(nullable = false, columnDefinition = "TEXT")
    public String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TipoToken tipo;

    @Column(nullable = false)
    public LocalDateTime expiracion;

    @Column(nullable = false)
    public Boolean revocado = false;

    @Column(name = "creado_en", nullable = false)
    public LocalDateTime creadoEn = LocalDateTime.now();

    public void revocar() {
        this.revocado = true;
    }

    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(this.expiracion);
    }

}