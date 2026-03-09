package br.com.convoquei.backend.core.organization.event.repository.views;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentMode;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantAvailabilityStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;

import java.time.Instant;
import java.time.Instant;
import java.util.UUID;

public record OrganizationEventMemberView(
        UUID id,
        String name,
        String description,
        String location,
        Instant startAt,
        Instant endAt,
        OrganizationEventStatus status,
        Integer availabilityLockBeforeStartInMinutes,
        Boolean allowAssignments,
        Long participantsCount,
        OrganizationEventAssignmentMode assignmentMode,
        OrganizationEventParticipantAvailabilityStatus participantEventAvailabilityStatus,
        OrganizationEventParticipantStatus participantEventStatus
) {}