package org.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequests {

    public record LoginRequest(
            @NotBlank(message = "El email es obligatorio")
            @Email(message = "El email no es válido")
            String email,
            @NotBlank(message = "La contraseña es obligatoria")
            String password) {}

    public record RegisterRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
            String fisrtname,
            @NotBlank(message = "El apellido es obligatorio")
            @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
            String lastname,
            @NotBlank(message = "El email es obligatorio")
            @Email(message = "El email no es válido")
            String email,
            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
            String password
    ) {}

    public record RefreshTokenRequest(
            @NotBlank(message = "El refresh token es obligatorio")
            String refreshToken
    ) {}

    public record LogoutRequest(
            @NotBlank(message = "El token es obligatorio")
            String refeshToken
    ) {}
}