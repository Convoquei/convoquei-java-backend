package br.com.convoquei.backend.core.organization.group.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddGroupParticipantRequest(
        @NotNull UUID memberId
) {
}

