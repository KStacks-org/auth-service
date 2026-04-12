package com.kaustack.auth.dto.response.genderize;

import com.kaustack.auth.model.Gender;
import lombok.Getter;

@Getter
public class GenderizeResponse {
    private String name;
    private Gender gender;
    private Double probability;
    private String source;
}
