package com.kaustack.auth.controller;

import com.kaustack.auth.dto.request.CreateFlagRequest;
import com.kaustack.auth.model.Flag;
import com.kaustack.auth.service.FlagService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('" + Flag.SUPER_ADMIN + "')")
public class FlagController {

    private final FlagService flagService;

    @GetMapping("/flags")
    public ResponseEntity<List<Flag>> listFlags() {
        List<Flag> flags = flagService.listFlags();
        return ResponseEntity.ok(flags);
    }

    @PostMapping("/flags")
    public ResponseEntity<Flag> createFlag(@Valid @RequestBody CreateFlagRequest request) {
        Flag flag = flagService.createFlag(request);
        return ResponseEntity.ok(flag);
    }

    @DeleteMapping("/flags/{flagId}")
    public ResponseEntity<Void> deleteFlag(@PathVariable UUID flagId) {
        flagService.deleteFlag(flagId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/flags/{flagId}")
    public ResponseEntity<Void> assignFlag(@PathVariable UUID userId, @PathVariable UUID flagId) {
        flagService.assignFlag(userId, flagId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}/flags/{flagId}")
    public ResponseEntity<Void> unassignFlag(@PathVariable UUID userId, @PathVariable UUID flagId) {
        flagService.unassignFlag(userId, flagId);
        return ResponseEntity.noContent().build();
    }
}
