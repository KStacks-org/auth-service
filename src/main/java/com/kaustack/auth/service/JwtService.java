package com.kaustack.auth.service;

import com.kaustack.auth.model.Flag;
import com.kaustack.auth.model.User;
import com.kaustack.jwt.JwtGenerator;
import com.kaustack.jwt.TokenType;
import com.kaustack.jwt.JwtUtils;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtGenerator jwtGenerator;
    private final JwtUtils jwtUtils;

    public String generateAccessToken(User user) {
        List<String> flagNames = user.getFlags().stream()
                .map(Flag::getName)
                .sorted()
                .toList();

        return jwtGenerator.generateToken(
                TokenType.ACCESS,
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getGender().name(),
                flagNames);
    }

    public String generateRefreshToken(User user) {
        return jwtGenerator.generateToken(
                TokenType.REFRESH,
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getGender().name());
    }

    public boolean validateAccessToken(String token) {
        return jwtUtils.validateToken(token, TokenType.ACCESS);
    }

    public boolean validateRefreshToken(String token) {
        return jwtUtils.validateToken(token, TokenType.REFRESH);
    }

    public UUID extractUserId(String token) {
        return jwtUtils.extractUserId(token);
    }

    public List<String> extractFlags(String token) {
        return jwtUtils.extractFlags(token);
    }
}
