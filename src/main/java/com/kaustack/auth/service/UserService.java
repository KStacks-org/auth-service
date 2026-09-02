package com.kaustack.auth.service;

import com.kaustack.auth.exception.ConflictException;
import com.kaustack.auth.exception.ResourceNotFoundException;
import com.kaustack.auth.model.Gender;
import com.kaustack.auth.model.User;
import com.kaustack.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GenderizeService genderizeService;

    @Transactional
    public User processOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Gender gender = genderizeService.predictGender(name);
                    User newUser = User.builder()
                            .email(email)
                            .name(name)
                            .gender(gender)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    // Used for the patch endpoint, in the case of the UNKNOWN gender, patching it to a specific gender
    @Transactional
    public User setGender(UUID userId, Gender gender) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getGender() != Gender.UNKNOWN) {
            throw new ConflictException("Gender has already been set and cannot be changed");
        }

        user.setGender(gender);
        return userRepository.save(user);
    }
}
