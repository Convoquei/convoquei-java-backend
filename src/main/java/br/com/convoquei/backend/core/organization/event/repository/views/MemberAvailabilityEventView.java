package br.com.convoquei.backend.core.organization.event.repository.views;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantAvailabilityStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MemberAvailabilityEventView(
        UUID eventId,
        String name,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        OrganizationEventStatus eventStatus,
        OrganizationEventParticipantStatus participantStatus,
        OrganizationEventParticipantAvailabilityStatus participantAvailabilityStatus
) {}
