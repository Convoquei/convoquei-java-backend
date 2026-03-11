package br.com.convoquei.backend.core.organization.event.api.dto.response;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEventCalledFunction;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentMode;
import br.com.convoquei.backend.core.organization.event.repository.views.OrganizationEventCalledFunctionView;

import java.util.UUID;

public record EventCalledFunctionResponse(
        UUID id,
        String name,
        String emoji,
        int minMembers,
        Integer maxMembers,
        OrganizationEventAssignmentMode assignmentMode
) {
    public static EventCalledFunctionResponse from(OrganizationEventCalledFunction calledFunction) {
        return new EventCalledFunctionResponse(
                calledFunction.getFunction().getId(),
                calledFunction.getFunction().getName(),
                calledFunction.getFunction().getEmoji(),
                calledFunction.getMinMembers(),
                calledFunction.getMaxMembers(),
                calledFunction.getAssignmentMode()
        );
    }

    public static EventCalledFunctionResponse from(OrganizationEventCalledFunctionView calledFunctionView) {
        return new EventCalledFunctionResponse(
                calledFunctionView.id(),
                calledFunctionView.name(),
                calledFunctionView.emoji(),
                calledFunctionView.minMembers(),
                calledFunctionView.maxMembers(),
                calledFunctionView.assignmentMode()
        );
    }
}
