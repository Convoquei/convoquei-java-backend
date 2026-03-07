package br.com.convoquei.backend.core.organization.organization.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrganizationRequest(
    @NotBlank @Size(min = 3, max = 70) String name
) { }