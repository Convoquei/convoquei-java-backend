package br.com.convoquei.backend.core.organization.event.api.dto.response;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEventParticipant;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantAvailabilityStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;
import br.com.convoquei.backend.core.organization.event.repository.views.OrganizationEventMemberView;
import br.com.convoquei.backend.core.organization.event.valueobject.OrganizationEventConfiguration;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public record OrganizationEventMembershipResponse(
        UUID id,
        String name,
        String description,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        OrganizationEventStatus status,
        int availability_lock_before_start_minutes,
        boolean allow_assignments,
        int participantsCount,
        OrganizationEventParticipantAvailabilityStatus membershipAvailabilityStatus,
        OrganizationEventParticipantStatus membershipStatus
) {
    public static OrganizationEventMembershipResponse buildFromEventAndMembership(OrganizationEvent event, UUID memberId) {
        OrganizationEventConfiguration configuration = event.getConfiguration();

        Optional<OrganizationEventParticipant> membership = event.findParticipantByMemberId(memberId);

        return new OrganizationEventMembershipResponse(
                event.getId(),
                configuration.getName(),
                configuration.getDescription(),
                configuration.getLocation(),
                event.getStartAt(),
                event.getEndAt(),
                event.getStatus(),
                configuration.getAvailabilityLockBeforeStartInMinutes(),
                event.isAllowAssignments(),
                event.getParticipants().size(),
                membership.map(OrganizationEventParticipant::getAvailabilityStatus).orElse(null),
                membership.map(OrganizationEventParticipant::getStatus).orElse(null)
        );
    }

    public static OrganizationEventMembershipResponse buildFromEventMembershipView(OrganizationEventMemberView view) {
        return new OrganizationEventMembershipResponse(
                view.id(),
                view.name(),
                view.description(),
                view.location(),
                view.startAt(),
                view.endAt(),
                view.status(),
                view.availabilityLockBeforeStartInMinutes(),
                view.allowAssignments(),
                Math.toIntExact(view.participantsCount()),
                view.participantEventAvailabilityStatus(),
                view.participantEventStatus()
        );
    }
}
