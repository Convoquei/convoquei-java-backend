package br.com.convoquei.backend.core.organization.member.api.dto.response;

import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.user.entity.User;

import java.util.List;

public record MyMemberhipResponse(
        String id,
        String name,
        String avatarUrl,
        String status,
        boolean isOwner,
        List<OrganizationMemberPermissionResponse> permissions
) {
    public static MyMemberhipResponse fromMember(OrganizationMember member) {
        User user = member.getUser();

        List<OrganizationMemberPermissionResponse> permissions = member.getPermissions().stream()
                .map(OrganizationMemberPermissionResponse::buildFromPermission)
                .toList();

        return new MyMemberhipResponse(member.getId().toString(), user.getName(), user.getAvatarUrl(), member.getStatus().name(), member.isOwner(), permissions);
    }
}
