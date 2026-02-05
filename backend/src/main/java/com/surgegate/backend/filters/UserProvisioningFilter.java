package com.surgegate.backend.filters;

import com.surgegate.backend.domain.entities.User;
import com.surgegate.backend.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProvisioningFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Jwt jwt) {

            // Fix: Auth0 IDs are Strings (e.g., "auth0|12345"), not UUIDs.
            String userId = jwt.getSubject();

            if (!userRepository.existsById(userId)) {
                log.info("New user detected. Provisioning user: {}", userId);

                User user = User.builder()
                        .id(userId)
                        // Auth0 standard claims often use "name" or "nickname"
                        .name(jwt.getClaimAsString("name"))
                        .email(jwt.getClaimAsString("email"))
                        .build();

                userRepository.save(user);
            }
        }

        filterChain.doFilter(request, response);
    }
}