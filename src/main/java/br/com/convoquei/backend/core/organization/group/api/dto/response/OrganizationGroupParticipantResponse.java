package br.com.convoquei.backend.core.organization.group.api.dto.response;

import br.com.convoquei.backend.core.organization.group.entity.OrganizationGroupParticipant;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrganizationGroupParticipantResponse(
        UUID id,
        UUID memberId,
        String memberName,
        String memberAvatarUrl,
        LocalDateTime joinedAt
) {
    public static OrganizationGroupParticipantResponse buildFromParticipant(OrganizationGroupParticipant participant) {
        return new OrganizationGroupParticipantResponse(
                participant.getId(),
                participant.getOrganizationMember().getId(),
                participant.getOrganizationMember().getUser().getName(),
                participant.getOrganizationMember().getUser().getAvatarUrl(),
                participant.getJoinedAt()
        );
    }
}

