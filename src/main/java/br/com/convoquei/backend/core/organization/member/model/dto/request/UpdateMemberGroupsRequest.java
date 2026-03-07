package br.com.convoquei.backend.core.organization.member.model.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UpdateMemberGroupsRequest(
        @NotNull List<UUID> groupIds
) {
}
