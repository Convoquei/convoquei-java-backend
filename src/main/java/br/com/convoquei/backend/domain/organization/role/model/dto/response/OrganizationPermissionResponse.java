package br.com.convoquei.backend.domain.organization.role.model.dto.response;

import br.com.convoquei.backend.domain.organization.role.model.enums.OrganizationPermission;

public record OrganizationPermissionResponse(
        String code,
        String title,
        String description
) {
    public static OrganizationPermissionResponse from(OrganizationPermission permission) {
        return new OrganizationPermissionResponse(permission.name(), permission.getTitle(), permission.getDescription());
    }
}

