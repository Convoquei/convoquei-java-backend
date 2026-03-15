package br.com.convoquei.backend.core.organization.event.api.dto.request;

import br.com.convoquei.backend.core.organization.event.enums.*;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UpdateOrganizationEventRequest(
        @NotBlank
        @Size(max = 70)
        String name,

        @Size(max = 2000)
        String description,

        @NotBlank
        @Size(max = 250)
        String location,

        @NotNull
        OrganizationEventAssignmentMode assignmentMode,

        @NotNull
        OrganizationEventCallMode callMode,

        OrganizationEventFunctionCallMode functionCallMode,

        @Min(0)
        int availabilityLockBeforeStartInMinutes,

        List<@NotNull CreateCreateOrganizationEventCalledFunctionRequest> calledFunctionIds,

        List<@NotNull UUID> calledGroupIds,

        @NotNull
        @Future
        OffsetDateTime startDate,

        @NotNull
        @Future
        OffsetDateTime endDate
) {
    public UpdateOrganizationEventRequest {
        calledFunctionIds = calledFunctionIds == null ? List.of() : calledFunctionIds;
        calledGroupIds = calledGroupIds == null ? List.of() : calledGroupIds;

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("A data final deve ser posterior à data inicial.");
        }
    }
}
