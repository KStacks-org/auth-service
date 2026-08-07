package com.kaustack.auth.controller;

import com.kaustack.jwt.JwtUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final JwtUtils jwtUtils;

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> jwks() {
        return ResponseEntity.ok(Map.of("keys", List.of(jwtUtils.getAccessPublicKeyAsJwk())));
    }
}
