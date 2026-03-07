package br.com.convoquei.backend.core.organization.role.api.dto.response;

import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;

import java.util.UUID;

public record OrganizationRoleMemberResponse(UUID id, String name, String avatarUrl) {
    public static OrganizationRoleMemberResponse buildForMember(OrganizationMember id) {
        return new OrganizationRoleMemberResponse(id.getId(), id.getUser().getName(), id.getUser().getAvatarUrl());
    }
}
