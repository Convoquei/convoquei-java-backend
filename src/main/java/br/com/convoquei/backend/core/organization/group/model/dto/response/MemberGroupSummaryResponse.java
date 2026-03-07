package br.com.convoquei.backend.core.organization.group.model.dto.response;
import java.util.UUID;
public record MemberGroupSummaryResponse(
        UUID id,
        String name,
        String emoji
) {
}
