package br.com.convoquei.backend.domain.organization.role.model.dto.response;

import br.com.convoquei.backend.domain.organization.member.model.entity.OrganizationMember;

import java.util.UUID;

public record OrganizationRoleMemberResponse(UUID id, String name, String avatarUrl) {
    public static OrganizationRoleMemberResponse buildForMember(OrganizationMember id) {
        return new OrganizationRoleMemberResponse(id.getId(), id.getUser().getName(), id.getUser().getAvatarUrl());
    }
}
