package br.com.convoquei.backend.core.organization.event.repository.queries;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantAvailabilityStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;

public record FindMemberEventsQuery(
        String eventNameStartsWith,
        OffsetDateTime startAfter,
        OffsetDateTime startBefore,
        List<OrganizationEventParticipantAvailabilityStatus> availabilityStatus,
        List<OrganizationEventParticipantStatus> participantStatus,
        List<OrganizationEventStatus> eventStatus,
        Pageable pageable
) {
}