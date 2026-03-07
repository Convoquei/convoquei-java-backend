package br.com.convoquei.backend.core.organization.member.model.dto.response;

import br.com.convoquei.backend.core.organization.group.model.dto.response.MemberGroupSummaryResponse;
import br.com.convoquei.backend.core.organization.member.model.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.member.model.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.core.organization.role.model.enums.OrganizationPermission;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record OrganizationMemberResponse(
        UUID id,
        String userName,
        String userAvatarUrl,
        OrganizationMemberStatus status,
        OffsetDateTime joinedAt,
        OffsetDateTime leftAt,
        List<OrganizationMemberPermissionResponse> permissions,
        List<MemberGroupSummaryResponse> groups
) {
    public static OrganizationMemberResponse buildFromMember(OrganizationMember member) {
        return buildFromMember(member, List.of());
    }

    public static OrganizationMemberResponse buildFromMember(OrganizationMember member, List<MemberGroupSummaryResponse> groups) {
        List<OrganizationMemberPermissionResponse> permissions = member.getPermissions().stream()
                .sorted(Comparator.comparingLong(OrganizationPermission::mask).reversed())
                .map(OrganizationMemberPermissionResponse::buildFromPermission)
                .toList();

        return new OrganizationMemberResponse(
                member.getId(),
                member.getUser().getName(),
                member.getUser().getAvatarUrl(),
                member.getStatus(),
                member.getJoinedAt(),
                member.getLeftAt(),
                permissions,
                groups
        );
    }
}
