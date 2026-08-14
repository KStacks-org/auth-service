package com.kaustack.auth.config;

import com.kaustack.auth.model.User;
import com.kaustack.auth.service.JwtService;
import com.kaustack.auth.service.UserService;

import com.kaustack.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        User user = userService.processOAuth2User(oAuth2User);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        ResponseCookie.ResponseCookieBuilder accessCookieBuilder = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(jwtUtils.extractMaxAge(accessToken));
        if (cookieDomain != null && !cookieDomain.isBlank()) {
            accessCookieBuilder.domain(cookieDomain);
        }
        ResponseCookie accessCookie = accessCookieBuilder.build();

        ResponseCookie.ResponseCookieBuilder refreshCookieBuilder = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(jwtUtils.extractMaxAge(refreshToken));
        if (cookieDomain != null && !cookieDomain.isBlank()) {
            refreshCookieBuilder.domain(cookieDomain);
        }
        ResponseCookie refreshCookie = refreshCookieBuilder.build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        String targetUrl = UriComponentsBuilder
                .fromUriString(redirectUri)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
