package br.com.convoquei.backend.core.organization.function.api.dto.response;

import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrganizationFunctionResponse(
        UUID id,
        String name,
        String emoji,
        int memberCount,
        LocalDateTime createdAt
) {
    public static OrganizationFunctionResponse buildFromFunction(OrganizationFunction function) {
        return new OrganizationFunctionResponse(
                function.getId(),
                function.getName(),
                function.getEmoji(),
                function.getMembers().size(),
                function.getCreatedAt()
        );
    }
}
