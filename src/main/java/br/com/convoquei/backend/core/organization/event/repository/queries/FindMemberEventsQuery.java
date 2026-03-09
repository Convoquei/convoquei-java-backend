package br.com.convoquei.backend.core.organization.event.repository.queries;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public record FindMemberEventsQuery(
        String eventNameStartsWith,
        Instant startAfter,
        Instant startBefore,
        List<OrganizationEventParticipantStatus> participantStatus,
        Pageable pageable
) {
}