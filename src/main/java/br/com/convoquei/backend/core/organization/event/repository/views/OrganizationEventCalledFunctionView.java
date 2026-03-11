package br.com.convoquei.backend.core.organization.event.repository.views;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentMode;

import java.util.UUID;

public record OrganizationEventCalledFunctionView(
    UUID id,
    String name,
    String emoji,
    Integer minMembers,
    Integer maxMembers,
    OrganizationEventAssignmentMode assignmentMode
) {
}
