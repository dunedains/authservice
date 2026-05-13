package org.authservice.dto.response;

import java.time.LocalDateTime;

public class AuthResponses {

    public record AuthResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn,
            UserInfo user
    ) {
        public static AuthResponse of(String accessToken, String refreshToken,
                                      long expiresIn, UserInfo user) {
            return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn, user);
        }
    }

    public record UserInfo(
            Long id,
            String email,
            String firstName,
            String lastName,
            String role,
            LocalDateTime createdAt
    ) {}

    public record MessageResponse(
            String message,
            boolean success
    ) {
        public static MessageResponse ok(String message) {
            return new MessageResponse(message, true);
        }

        public static MessageResponse error(String message) {
            return new MessageResponse(message, false);
        }
    }
}
