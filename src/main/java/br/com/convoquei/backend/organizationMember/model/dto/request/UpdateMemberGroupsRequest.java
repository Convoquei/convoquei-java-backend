package br.com.convoquei.backend.organizationMember.model.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UpdateMemberGroupsRequest(
        @NotNull List<UUID> groupIds
) {
}
