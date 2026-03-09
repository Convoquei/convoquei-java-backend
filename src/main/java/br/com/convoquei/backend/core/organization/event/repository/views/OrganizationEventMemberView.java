package br.com.convoquei.backend.core.organization.event.repository.views;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentMode;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantAvailabilityStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrganizationEventMemberView(
        UUID id,
        String name,
        String description,
        String location,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        OrganizationEventStatus status,
        Integer availabilityLockBeforeStartInMinutes,
        Boolean allowAssignments,
        Long participantsCount,
        OrganizationEventAssignmentMode assignmentMode,
        OrganizationEventParticipantAvailabilityStatus participantEventAvailabilityStatus,
        OrganizationEventParticipantStatus participantEventStatus
) {}