package br.com.convoquei.backend.core.organization.role.api.dto.response;

import br.com.convoquei.backend.core.organization.role.entity.OrganizationRole;
import br.com.convoquei.backend.core.organization.role.helpers.PermissionMask;

import java.util.List;
import java.util.UUID;

public record OrganizationRoleResponse(
        UUID id,
        String name,
        boolean isSystem,
        List<OrganizationPermissionResponse> permissions
) {
    public static OrganizationRoleResponse from(OrganizationRole role) {
        List<OrganizationPermissionResponse> permissions = PermissionMask.fromMask(role.getPermissionsMask())
                .stream()
                .sorted((a, b) -> Long.compare(b.mask(), a.mask()))
                .map(OrganizationPermissionResponse::from)
                .toList();

        return new OrganizationRoleResponse(
                role.getId(),
                role.getName(),
                role.isSystem(),
                permissions
        );
    }
}

