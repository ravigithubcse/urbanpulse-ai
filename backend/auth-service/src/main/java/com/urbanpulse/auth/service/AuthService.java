package com.urbanpulse.auth.service;

import com.urbanpulse.auth.model.Role;
import com.urbanpulse.auth.model.User;
import com.urbanpulse.auth.repository.RoleRepository;
import com.urbanpulse.auth.repository.UserRepository;
import com.urbanpulse.auth.security.JwtTokenProvider;
import com.urbanpulse.auth.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Authentication service handling login, registration, and token management.
 * Implements account lockout after failed attempts for security.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtService jwtService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MINUTES = 30;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Check account lockout
        if (user.isLocked()) {
            long minutesRemaining = java.time.Duration.between(
                    LocalDateTime.now(), user.getLockedUntil()).toMinutes();
            throw new LockedException(
                    "Account is locked. Try again in " + minutesRemaining + " minutes");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Reset failed attempts on successful login
            userRepository.updateLastLogin(user.getId(), LocalDateTime.now());

            String accessToken = jwtTokenProvider.generateAccessToken(userDetails, user.getId());
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails, user.getId());

            // Store session in Redis
            redisTemplate.opsForValue().set(
                    "session:" + user.getId(),
                    accessToken,
                    jwtTokenProvider.getExpirationTime(),
                    TimeUnit.MILLISECONDS
            );

            log.info("User logged in successfully: {}", user.getEmail());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getExpirationTime() / 1000)
                    .user(mapToUserDto(user))
                    .build();

        } catch (BadCredentialsException e) {
            handleFailedLogin(user);
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        Role viewerRole = roleRepository.findByName(Role.RoleName.VIEWER)
                .orElseThrow(() -> new IllegalStateException("Default role not found"));

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .department(request.getDepartment())
                .isActive(true)
                .emailVerified(false)
                .roles(Set.of(viewerRole))
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        // Auto-login after registration
        return login(new LoginRequest(request.getEmail(), request.getPassword()));
    }

    @Transactional(readOnly = true)
    public LoginResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateTokenSignature(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid token type");
        }

        UUID userId = jwtTokenProvider.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!user.getIsActive()) {
            throw new DisabledException("User account is deactivated");
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).toArray(String[]::new))
                .build();

        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails, user.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails, user.getId());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime() / 1000)
                .user(mapToUserDto(user))
                .build();
    }

    @Transactional
    public void logout(String token) {
        jwtService.blacklistToken(token);
        try {
            UUID userId = jwtTokenProvider.extractUserId(token);
            redisTemplate.delete("session:" + userId);
            log.info("User logged out: {}", userId);
        } catch (Exception e) {
            log.warn("Could not extract user ID during logout");
        }
    }

    private void handleFailedLogin(User user) {
        userRepository.incrementFailedLoginAttempts(user.getId());
        int attempts = user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() + 1 : 1;

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            LocalDateTime lockedUntil = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
            userRepository.lockAccount(user.getId(), lockedUntil);
            log.warn("Account locked for user: {} due to {} failed attempts", user.getEmail(), attempts);
        }
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).toList())
                .isActive(user.getIsActive())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
