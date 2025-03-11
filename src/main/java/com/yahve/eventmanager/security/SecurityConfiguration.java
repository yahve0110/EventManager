package com.yahve.eventmanager.security;

import com.yahve.eventmanager.security.jwt.JwtTokenFilter;
import com.yahve.eventmanager.user.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

  private static final String ROLE_ADMIN = UserRole.ADMIN.name();
  private static final String ROLE_USER = UserRole.USER.name();
  private final CustomUserDetailService customUserDetailService;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final AccessDeniedHandler customAccessDeniedHandler;
  private final JwtTokenFilter jwtTokenFilter;

  public SecurityConfiguration(
    CustomUserDetailService customUserDetailService,
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
    AccessDeniedHandler customAccessDeniedHandler,
    JwtTokenFilter jwtTokenFilter
  ) {
    this.customUserDetailService = customUserDetailService;
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    this.customAccessDeniedHandler = customAccessDeniedHandler;
    this.jwtTokenFilter = jwtTokenFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return http
      .formLogin(AbstractHttpConfigurer::disable)
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .authorizeHttpRequests(authorizeRequests ->
        authorizeRequests
          .requestMatchers(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/openapi.yaml"
          ).permitAll()

          .requestMatchers(HttpMethod.POST, "/events/**").hasAuthority(ROLE_USER)
          .requestMatchers(HttpMethod.DELETE, "/events/{id}").hasAnyAuthority(ROLE_ADMIN, ROLE_USER)
          .requestMatchers(HttpMethod.PUT, "/events/{id}").hasAnyAuthority(ROLE_ADMIN, ROLE_USER)
          .requestMatchers(HttpMethod.GET, "/events/{id}").hasAnyAuthority(ROLE_ADMIN, ROLE_USER)
          .requestMatchers(HttpMethod.GET, "/events/my").hasAnyAuthority(ROLE_USER)


          .requestMatchers(HttpMethod.POST, "/locations").hasAuthority(ROLE_ADMIN)
          .requestMatchers(HttpMethod.DELETE, "/locations/**").hasAuthority(ROLE_ADMIN)
          .requestMatchers(HttpMethod.PUT, "/locations/**").hasAuthority(ROLE_ADMIN)

          .requestMatchers(HttpMethod.GET, "/users/{id}").hasAuthority(ROLE_ADMIN)

          .requestMatchers(HttpMethod.POST, "/users").permitAll()
          .requestMatchers(HttpMethod.POST, "/users/auth").permitAll()

          .anyRequest().authenticated()
      )
      .exceptionHandling(exception ->
        exception.authenticationEntryPoint(customAuthenticationEntryPoint)
          .accessDeniedHandler(customAccessDeniedHandler)
      )
      .addFilterBefore(jwtTokenFilter, AnonymousAuthenticationFilter.class)
      .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    var authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(customUserDetailService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
