package br.com.convoquei.backend.core.organization.event.api.dto.request;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentModeEnum;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CreateOrganizationEventRequest(
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
        LocalDateTime startDate,

        @NotNull
        LocalDateTime endDate,

        @Min(0)
        int availabilityLockBeforeStartInMinutes,

        @NotNull
        OrganizationEventAssignmentModeEnum assignmentMode
) {}