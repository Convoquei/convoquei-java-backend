package br.com.convoquei.backend.core.organization.member.api.dto.response;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantAvailabilityStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MemberAvailabilityEventResponse(
        UUID eventId,
        String name,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        OrganizationEventStatus eventStatus,
        OrganizationEventParticipantStatus participantStatus,
        OrganizationEventParticipantAvailabilityStatus participantAvailabilityStatus
) {}
