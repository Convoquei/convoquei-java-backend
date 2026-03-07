package br.com.convoquei.backend.core.organization.member.api.dto.response;

import br.com.convoquei.backend.core.organization.function.api.dto.response.MemberFunctionSummaryResponse;
import br.com.convoquei.backend.core.organization.group.api.dto.response.MemberGroupSummaryResponse;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.member.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record OrganizationMemberResponse(
        UUID id,
        String userName,
        String userAvatarUrl,
        OrganizationMemberStatus status,
        LocalDateTime joinedAt,
        LocalDateTime leftAt,
        List<OrganizationMemberPermissionResponse> permissions,
        List<MemberGroupSummaryResponse> groups,
        List<MemberFunctionSummaryResponse> functions
) {
    public static OrganizationMemberResponse buildFromMember(OrganizationMember member) {
        return buildFromMember(member, List.of(), List.of());
    }

    public static OrganizationMemberResponse buildFromMember(OrganizationMember member, List<MemberGroupSummaryResponse> groups) {
        return buildFromMember(member, groups, List.of());
    }

    public static OrganizationMemberResponse buildFromMember(OrganizationMember member, List<MemberGroupSummaryResponse> groups, List<MemberFunctionSummaryResponse> functions) {
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
                groups,
                functions
        );
    }
}
