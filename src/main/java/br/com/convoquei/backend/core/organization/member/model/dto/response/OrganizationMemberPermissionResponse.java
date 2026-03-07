package br.com.convoquei.backend.core.organization.member.model.dto.response;

import br.com.convoquei.backend.core.organization.role.model.enums.OrganizationPermission;

public record OrganizationMemberPermissionResponse(
        String code,
        String description
) {
    public static OrganizationMemberPermissionResponse buildFromPermission(OrganizationPermission permission) {
        return new OrganizationMemberPermissionResponse(permission.name(), permission.getDescription());
    }
}
