package br.com.convoquei.backend.core.organization.event.repository.views;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantAvailabilityStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrganizationEventMemberView(
        UUID id,
        String name,
        String description,
        String location,
        LocalDateTime startAt,
        LocalDateTime endAt,
        OrganizationEventStatus status,
        Integer availabilityLockBeforeStartInMinutes,
        Boolean allowAssignments,
        Long participantsCount,
        OrganizationEventParticipantAvailabilityStatus participantEventAvailabilityStatus,
        OrganizationEventParticipantStatus participantEventStatus
) {}