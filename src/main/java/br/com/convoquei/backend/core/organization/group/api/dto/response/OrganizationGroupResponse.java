package br.com.convoquei.backend.core.organization.group.api.dto.response;

import br.com.convoquei.backend.core.organization.group.entity.OrganizationGroup;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrganizationGroupResponse(
        UUID id,
        String name,
        String emoji,
        int participantCount,
        OffsetDateTime createdAt
) {
    public static OrganizationGroupResponse buildFromGroup(OrganizationGroup group) {
        return new OrganizationGroupResponse(
                group.getId(),
                group.getName(),
                group.getEmoji(),
                group.getParticipants().size(),
                group.getCreatedAt()
        );
    }
}

