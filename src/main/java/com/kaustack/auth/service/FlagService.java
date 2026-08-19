package com.kaustack.auth.service;

import com.kaustack.auth.dto.request.CreateFlagRequest;
import com.kaustack.auth.exception.ConflictException;
import com.kaustack.auth.exception.ResourceNotFoundException;
import com.kaustack.auth.model.Flag;
import com.kaustack.auth.model.User;
import com.kaustack.auth.repository.FlagRepository;
import com.kaustack.auth.repository.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlagService {

    private final FlagRepository flagRepository;
    private final UserRepository userRepository;

    public List<Flag> listFlags() {
        return flagRepository.findAll();
    }

    @Transactional
    public Flag createFlag(CreateFlagRequest request) {
        if (flagRepository.existsByName(request.name())) {
            throw new ConflictException("Flag '" + request.name() + "' already exists");
        }

        Flag flag = Flag.builder()
                .name(request.name())
                .description(request.description())
                .build();

        try {
            return flagRepository.saveAndFlush(flag);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Flag '" + request.name() + "' already exists");
        }
    }

    @Transactional
    public void deleteFlag(UUID flagId) {
        Flag flag = getFlag(flagId);

        for (User user : userRepository.findAllByFlagsId(flagId)) {
            user.getFlags().remove(flag);
        }

        flagRepository.delete(flag);
    }

    @Transactional
    public void assignFlag(UUID userId, UUID flagId) {
        User user = getUser(userId);
        Flag flag = getFlag(flagId);

        user.getFlags().add(flag);
    }

    @Transactional
    public void unassignFlag(UUID userId, UUID flagId) {
        User user = getUser(userId);
        Flag flag = getFlag(flagId);

        user.getFlags().remove(flag);
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Flag getFlag(UUID flagId) {
        return flagRepository.findById(flagId)
                .orElseThrow(() -> new ResourceNotFoundException("Flag not found"));
    }
}
