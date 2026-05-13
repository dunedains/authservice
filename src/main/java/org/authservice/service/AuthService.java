package org.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.authservice.dto.request.AuthRequests.LoginRequest;
import org.authservice.dto.request.AuthRequests.LogoutRequest;
import org.authservice.dto.request.AuthRequests.RefreshTokenRequest;
import org.authservice.dto.request.AuthRequests.RegisterRequest;
import org.authservice.dto.response.AuthResponses.AuthResponse;
import org.authservice.dto.response.AuthResponses.MessageResponse;
import org.authservice.dto.response.AuthResponses.UserInfo;
import org.authservice.expection.AuthExeption;
import org.authservice.model.RefreshToken;
import org.authservice.model.Role;
import org.authservice.model.User;
import org.authservice.repository.RefeshTokenRepository;
import org.authservice.repository.UserRepository;
import org.authservice.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefeshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthExeption("El email ya está registrado: " + request.email());
        }

        User user = User.builder()
                .firstName(request.fisrtname())
                .lastName(request.lastname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Nuevo usuario registrado: {}", savedUser.getEmail());

        return buildAuthResponse(savedUser);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException e) {
            throw new AuthExeption("Email o contraseña incorrectos");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthExeption("Usuario no encontrado"));

        refreshTokenRepository.revokeAllUserTokens(user);
        log.info("Login exitoso: {}", user.getEmail());

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.refreshToken())
                .orElseThrow(() -> new AuthExeption("Refresh token no encontrado"));

        if (!refreshToken.isValid()) {
            throw new AuthExeption("Refresh token expirado o revocado");
        }

        User user = refreshToken.getUser();
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.info("Token refrescado para: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    @Transactional
    public MessageResponse logout(LogoutRequest request) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.refeshToken())
                .orElseThrow(() -> new AuthExeption("Refresh token no encontrado"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.info("Logout para: {}", refreshToken.getUser().getEmail());
        return MessageResponse.ok("Sesión cerrada correctamente");
    }

    public UserInfo getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExeption("Usuario no encontrado"));
        return toUserInfo(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshTokenValue = createRefreshToken(user);
        return AuthResponse.of(accessToken, refreshTokenValue, jwtService.getExpirationTime(), toUserInfo(user));
    }

    private String createRefreshToken(User user) {
        String tokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .build();
        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }

    private UserInfo toUserInfo(User user) {
        return new UserInfo(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}