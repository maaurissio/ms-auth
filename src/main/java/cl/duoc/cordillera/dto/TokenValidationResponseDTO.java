package cl.duoc.cordillera.dto;

import java.util.UUID;

public class TokenValidationResponseDTO {

    public Boolean valido;
    public UUID usuarioId;
    public String email;

    public TokenValidationResponseDTO(Boolean valido, UUID usuarioId, String email) {
        this.valido = valido;
        this.usuarioId = usuarioId;
        this.email = email;
    }
}