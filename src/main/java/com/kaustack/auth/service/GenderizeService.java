package com.kaustack.auth.service;

import com.kaustack.auth.dto.response.genderize.GenderizeResponse;
import com.kaustack.auth.model.Gender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenderizeService {

    private final RestTemplate restTemplate;

    @Value("${genderize.service.url}")
    private String genderizeUrl;

    public Gender predictGender(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return Gender.UNKNOWN;
        }

        String firstName = fullName.trim().split("\\s+")[0];

        String url = UriComponentsBuilder.fromUriString(genderizeUrl)
                .path("/genderize")
                .queryParam("name", firstName)
                .toUriString();

        try {
            GenderizeResponse response = restTemplate.getForObject(url, GenderizeResponse.class);
            if (response == null || response.getGender() == null) {
                return Gender.UNKNOWN;
            }
            return response.getGender();
        } catch (Exception e) {
            log.warn("Gender prediction failed for name '{}': {}", firstName, e.getMessage());
            return Gender.UNKNOWN;
        }
    }
}
