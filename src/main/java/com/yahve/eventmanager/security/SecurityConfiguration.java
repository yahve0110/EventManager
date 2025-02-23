package com.yahve.eventmanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

  @Autowired
  private CustomUserDetailService customUserDetailService;

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
//        .requestMatchers(HttpMethod.POST, "/locations")
//          .hasAnyAuthority("ADMIN")
        .requestMatchers(HttpMethod.POST,"/users")
        .permitAll()
        .anyRequest().authenticated()
    )
    .httpBasic(Customizer.withDefaults())
    .build();
}

@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception  {
  return configuration.getAuthenticationManager();
}

@Bean
public AuthenticationProvider authenticationProvider() {
  var authProvider = new DaoAuthenticationProvider();
  authProvider.setUserDetailsService(customUserDetailService);
  authProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
  return  authProvider;
}

}
