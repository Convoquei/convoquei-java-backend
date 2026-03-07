package br.com.convoquei.backend.domain.organization.group.model.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddGroupParticipantRequest(
        @NotNull UUID memberId
) {
}

