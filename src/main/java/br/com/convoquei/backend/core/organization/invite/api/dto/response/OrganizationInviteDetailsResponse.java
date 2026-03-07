package br.com.convoquei.backend.core.organization.invite.api.dto.response;

import br.com.convoquei.backend.core.organization.invite.entity.OrganizationInvite;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrganizationInviteDetailsResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("email") String invitedEmail,
        @JsonProperty("organization") String organizationName,
        @JsonProperty("invitedBy") String invitedByUserName,
        @JsonProperty("invitedAt") LocalDateTime createdAt,
        @JsonProperty("expiresAt") LocalDateTime expiresAt
) {
    public static OrganizationInviteDetailsResponse buildFromInvite(OrganizationInvite invite) {
        return new OrganizationInviteDetailsResponse(
                invite.getId(),
                invite.getInvitedEmail(),
                invite.getOrganization().getName(),
                invite.getInvitedBy().getUser().getName(),
                invite.getCreatedAt(),
                invite.getExpiresAt()
        );
    }
}
