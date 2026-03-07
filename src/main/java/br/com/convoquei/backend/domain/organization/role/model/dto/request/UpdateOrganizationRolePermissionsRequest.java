package br.com.convoquei.backend.domain.organization.role.model.dto.request;

import java.util.List;

public record UpdateOrganizationRolePermissionsRequest(
        List<String> permissions
) {
    public UpdateOrganizationRolePermissionsRequest {
        if (permissions == null) permissions = List.of();
    }
}

