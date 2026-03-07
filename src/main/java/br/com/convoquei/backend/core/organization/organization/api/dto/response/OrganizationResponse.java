package br.com.convoquei.backend.core.organization.organization.api.dto.response;

import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.organization.organization.enums.OrganizationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String slug,
        String photoUrl,
        OrganizationStatus status,
        int totalMembers,
        LocalDateTime createdAt
) {
    public static OrganizationResponse buildFromOrganization(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                organization.getPhotoUrl(),
                organization.getStatus(),
                organization.getMembers().size(),
                organization.getCreatedAt()
        );
    }
}
