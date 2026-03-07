package br.com.convoquei.backend.core.organization.role.api.dto.response;

import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;

public record OrganizationPermissionResponse(
        String code,
        String title,
        String description
) {
    public static OrganizationPermissionResponse from(OrganizationPermission permission) {
        return new OrganizationPermissionResponse(permission.name(), permission.getTitle(), permission.getDescription());
    }
}

