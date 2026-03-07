package br.com.convoquei.backend.core.organization.function.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UpdateMemberFunctionsRequest(
        @NotNull List<UUID> functionIds
) {
}
