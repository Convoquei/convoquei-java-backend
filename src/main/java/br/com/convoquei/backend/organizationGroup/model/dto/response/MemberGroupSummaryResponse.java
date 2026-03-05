package br.com.convoquei.backend.organizationGroup.model.dto.response;
import java.util.UUID;
public record MemberGroupSummaryResponse(
        UUID id,
        String name,
        String emoji
) {
}
