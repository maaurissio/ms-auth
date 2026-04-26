package cl.duoc.cordillera.entity;

import cl.duoc.cordillera.enums.EstadoUsuario;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "credenciales")
public class Credencial extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    // Referencia lógica al usuario real que vive en MS-Users.
    // No es FK porque cada microservicio tiene su propia BD.
    @Column(name = "usuario_id", nullable = false, unique = true)
    public UUID usuarioId;

    @Column(nullable = false, unique = true)
    public String email;

    // No se expone ni se guarda la contraseña real.
    // Solo se guarda el hash de la contraseña.
    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @Column(name = "creado_en", nullable = false)
    public LocalDateTime creadoEn = LocalDateTime.now();

    @Column(name = "actualizado_en")
    public LocalDateTime actualizadoEn;

    public boolean validarPassword(String password) {
        return org.mindrot.jbcrypt.BCrypt.checkpw(password, this.passwordHash);
    }

    public void activar() {
        this.estado = EstadoUsuario.ACTIVO;
        this.actualizadoEn = LocalDateTime.now();
    }

    public void desactivar() {
        this.estado = EstadoUsuario.INACTIVO;
        this.actualizadoEn = LocalDateTime.now();
    }
}