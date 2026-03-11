package br.com.convoquei.backend.core.organization.event.api.dto.request;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCreateOrganizationEventCalledFunctionRequest(
        @NotNull UUID id,

        @NotNull
        @Min(1)
        int minMembers,

        Integer maxMembers,
        OrganizationEventAssignmentMode assignmentMode
) {
    public CreateCreateOrganizationEventCalledFunctionRequest {
        if (maxMembers != null && maxMembers < minMembers) {
            throw new IllegalArgumentException("O número máximo de membros deve ser maior ou igual ao número mínimo.");
        }
    }
}