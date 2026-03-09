package br.com.convoquei.backend.core.organization.event.api.dto.request;

import br.com.convoquei.backend.core.organization.event.api.dto.request.interfaces.CreateOrganizationEventContract;
import br.com.convoquei.backend.core.organization.event.enums.*;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateCreateOrganizationEventRequest(
        @NotBlank
        @Size(max = 70)
        String name,

        @NotBlank
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

        List<@NotNull UUID> calledFunctionIds,

        List<@NotNull UUID> calledGroupIds,

        @NotNull
        @Future
        Instant startDate,

        @NotNull
        @Future
        Instant endDate

) implements CreateOrganizationEventContract {
    public CreateCreateOrganizationEventRequest {
        calledFunctionIds = calledFunctionIds == null ? List.of() : calledFunctionIds;
        calledGroupIds = calledGroupIds == null ? List.of() : calledGroupIds;

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("A data final deve ser posterior à data inicial.");
        }
    }
}