package com.urbanpulse.auth.security;

import com.urbanpulse.auth.model.User;
import com.urbanpulse.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation that loads user data
 * from PostgreSQL and maps roles to Spring Security authorities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is deactivated");
        }

        var authorities = user.getRoles().stream()
                .flatMap(role -> {
                    var perms = role.getPermissions().stream()
                            .map(perm -> new SimpleGrantedAuthority("PERMISSION_" + perm.name()));
                    var roleAuth = new SimpleGrantedAuthority("ROLE_" + role.getName().name());
                    return java.util.stream.Stream.concat(java.util.stream.Stream.of(roleAuth), perms);
                })
                .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(user.isLocked())
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
    }
}
