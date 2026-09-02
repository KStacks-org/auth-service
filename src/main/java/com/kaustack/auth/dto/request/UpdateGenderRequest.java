package com.kaustack.auth.dto.request;

import com.kaustack.auth.model.Gender;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record UpdateGenderRequest(
        @NotNull(message = "must be MALE or FEMALE")
        Gender gender
) {
    /**
     * UNKNOWN is only ever assigned by name prediction, so it cannot be chosen.
     */
    @AssertTrue(message = "must be MALE or FEMALE")
    public boolean isSelectableGender() {
        return gender == Gender.MALE || gender == Gender.FEMALE;
    }
}
