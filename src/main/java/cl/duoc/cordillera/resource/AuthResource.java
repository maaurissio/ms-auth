package cl.duoc.cordillera.resource;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import cl.duoc.cordillera.dto.LoginRequestDTO;
import cl.duoc.cordillera.dto.LoginResponseDTO;
import cl.duoc.cordillera.dto.RefreshTokenRequestDTO;
import cl.duoc.cordillera.dto.TokenValidationResponseDTO;
import cl.duoc.cordillera.service.AuthService;
import java.util.UUID;
import jakarta.annotation.security.RolesAllowed;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    JsonWebToken jwt;

    //LOGIN
    @POST
    @Path("/login")
    public LoginResponseDTO login(LoginRequestDTO request) {
        return authService.login(request);
    }

    //REFRESH
    @POST
    @Path("/refresh")
    public LoginResponseDTO refresh(RefreshTokenRequestDTO request) {
        return authService.refresh(request);
    }

    //VALIDAR TOKEN (para BFF)
    @POST
    @Path("/validate")
    @RolesAllowed("USER")
    @SecurityRequirement(name = "BearerAuth")
    public TokenValidationResponseDTO validar() {
        return new TokenValidationResponseDTO(
                true,
                UUID.fromString(jwt.getSubject()),
                jwt.getClaim("email")
        );
    }
    //LOGOUT
    @POST
    @Path("/logout")
    public void logout(RefreshTokenRequestDTO request) {
        authService.logout(request.refreshToken);
    }

    //HEALTH CHECK
    @GET
    @Path("/health")
    public String health() {
        return "MS-Auth funcionando";
    }


}