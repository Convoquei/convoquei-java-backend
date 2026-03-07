package br.com.convoquei.backend.core.organization.function.api.dto.response;

import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunctionMember;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrganizationFunctionMemberResponse(
        UUID id,
        UUID memberId,
        String memberName,
        String memberAvatarUrl,
        LocalDateTime joinedAt
) {
    public static OrganizationFunctionMemberResponse buildFromFunctionMember(OrganizationFunctionMember functionMember) {
        return new OrganizationFunctionMemberResponse(
                functionMember.getId(),
                functionMember.getOrganizationMember().getId(),
                functionMember.getOrganizationMember().getUser().getName(),
                functionMember.getOrganizationMember().getUser().getAvatarUrl(),
                functionMember.getJoinedAt()
        );
    }
}
