package com.kaustack.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateFlagRequest(
        @NotBlank
        @Size(max = 64)
        @Pattern(regexp = "^[a-z0-9][a-z0-9-]*$",
                message = "must be lowercase alphanumerics or hyphens, e.g. super-admin")
        String name,

        @Size(max = 255)
        String description
) {
}
