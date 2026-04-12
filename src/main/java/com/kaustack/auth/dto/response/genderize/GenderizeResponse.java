package com.kaustack.auth.dto.response.genderize;

import lombok.Getter;

@Getter
public class GenderizeResponse {
    private String name;
    private String gender;
    private Double probability;
    private String source;
}
