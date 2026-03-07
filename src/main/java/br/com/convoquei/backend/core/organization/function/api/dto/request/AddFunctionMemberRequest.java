package br.com.convoquei.backend.core.organization.function.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddFunctionMemberRequest(
        @NotNull UUID memberId
) {
}
