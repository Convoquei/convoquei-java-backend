package br.com.convoquei.backend.core.organization.role.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrganizationRoleRequest(
        @NotBlank @Size(min = 1, max = 80) String name,
        List<String> permissions
) {
    public CreateOrganizationRoleRequest {
        if (permissions == null) permissions = List.of();
    }
}

