package com.yahve.eventmanager.security.jwt;

import com.yahve.eventmanager.entity.User;
import com.yahve.eventmanager.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

  private final JwtTokenManager jwtTokenManager;
  private final UserService userService;


  public JwtTokenFilter(JwtTokenManager jwtTokenManager,@Lazy UserService userService) {
    this.jwtTokenManager = jwtTokenManager;
    this.userService = userService;
  }


  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
    throws ServletException, IOException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    var token = authHeader.substring(7);

    String loginFromToken;
    try {
      loginFromToken = jwtTokenManager.getLogiFromTokenn(token);
    } catch (Exception e) {
      logger.error("Error while reading jwt", e.getMessage());
      filterChain.doFilter(request, response);
      return;
    }

    User user = userService.findByLogin(loginFromToken);

    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
      user,
      null,
      List.of(new SimpleGrantedAuthority(user.getRole()))));
    filterChain.doFilter(request, response);
  }
}
