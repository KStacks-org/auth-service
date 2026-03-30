package com.kaustack.auth.config;

import com.kaustack.auth.dto.response.ErrorResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    @Value("${app.oauth2.hosted-domain:}")
    private String hostedDomain;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/login", "/refresh").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ACTUATOR")
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(authorizationRequestResolver()))
                        .successHandler(oAuth2LoginSuccessHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            ErrorResponse error = ErrorResponse.of(
                                    HttpStatus.UNAUTHORIZED.value(),
                                    "Unauthorized",
                                    "UNAUTHORIZED",
                                    "Authentication required",
                                    request.getRequestURI()
                            );
                            response.getWriter().write(objectMapper.writeValueAsString(error));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            ErrorResponse error = ErrorResponse.of(
                                    HttpStatus.FORBIDDEN.value(),
                                    "Forbidden",
                                    "ACCESS_DENIED",
                                    "Access denied",
                                    request.getRequestURI()
                            );
                            response.getWriter().write(objectMapper.writeValueAsString(error));
                        }));

        return http.build();
    }

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver() {
        DefaultOAuth2AuthorizationRequestResolver resolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                        "/oauth2/authorization"
                );

        resolver.setAuthorizationRequestCustomizer(this::customizeAuthorizationRequest);
        return resolver;
    }

    private void customizeAuthorizationRequest(OAuth2AuthorizationRequest.Builder builder) {
        if (hostedDomain != null && !hostedDomain.isEmpty()) {
            // Add the hd parameter to restrict login to specific domain
            Map<String, Object> additionalParameters = new HashMap<>();
            additionalParameters.put("hd", hostedDomain);
            builder.additionalParameters(additionalParameters);
        }
    }

}
