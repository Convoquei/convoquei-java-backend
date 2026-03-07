package br.com.convoquei.backend.core.organization.function.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFunctionRequest(
        @NotBlank @Size(max = 70) String name,
        @Size(max = 10) String emoji
) {
}
