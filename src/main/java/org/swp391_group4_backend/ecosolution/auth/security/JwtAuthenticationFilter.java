package org.swp391_group4_backend.ecosolution.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;
import org.swp391_group4_backend.ecosolution.auth.repository.UserAuthRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserAuthRepository userAuthRepository;

  public JwtAuthenticationFilter(JwtService jwtService, UserAuthRepository userAuthRepository) {
    this.jwtService = jwtService;
    this.userAuthRepository = userAuthRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring("Bearer ".length());
    if (!jwtService.isTokenValid(token) || SecurityContextHolder.getContext().getAuthentication() != null) {
      filterChain.doFilter(request, response);
      return;
    }

    UUID userId = jwtService.parseUserId(token);
    Optional<UserAuth> userAuthOptional = userAuthRepository.findByUserId(userId);
    if (userAuthOptional.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    UserAuth userAuth = userAuthOptional.get();
    String roleName = userAuth.getUser().getRole().name();
    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleName));

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userAuth.getUsername(),
        null,
        authorities
    );
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }
}

