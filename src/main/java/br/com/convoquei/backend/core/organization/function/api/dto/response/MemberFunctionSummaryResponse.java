package br.com.convoquei.backend.core.organization.function.api.dto.response;

import java.util.UUID;

public record MemberFunctionSummaryResponse(
        UUID id,
        String name,
        String emoji
) {
}
