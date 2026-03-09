package br.com.convoquei.backend.core.organization.event.api.dto.response;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEventParticipant;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentMode;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantAvailabilityStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;
import br.com.convoquei.backend.core.organization.event.repository.views.OrganizationEventMemberView;
import br.com.convoquei.backend.core.organization.event.valueobject.OrganizationEventConfiguration;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public record OrganizationEventMembershipResponse(
        UUID id,
        String name,
        String description,
        String location,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        OrganizationEventStatus status,
        int availabilityLockBeforeStartMinutes,
        long availabilityLockEpochSeconds,
        boolean allowAssignment,
        int participantsCount,
        OrganizationEventAssignmentMode assignmentMode,
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
                event.getTimeUntilAvailabilityLock().toSeconds(),
                event.isAllowAssignments(),
                event.getParticipants().size(),
                configuration.getAssignmentMode(),
                membership.map(OrganizationEventParticipant::getAvailabilityStatus).orElse(null),
                membership.map(OrganizationEventParticipant::getStatus).orElse(null)
        );
    }

    public static OrganizationEventMembershipResponse buildFromEventMembershipView(OrganizationEventMemberView view) {
        long secondsUntilLock = view.availabilityLockBeforeStartInMinutes() != null
                ? OffsetDateTime.now().until(view.startAt().minusSeconds(view.availabilityLockBeforeStartInMinutes() * 60L), ChronoUnit.SECONDS)
                : 0L;

        return new OrganizationEventMembershipResponse(
                view.id(),
                view.name(),
                view.description(),
                view.location(),
                view.startAt(),
                view.endAt(),
                view.status(),
                view.availabilityLockBeforeStartInMinutes(),
                secondsUntilLock,
                view.allowAssignments(),
                Math.toIntExact(view.participantsCount()),
                view.assignmentMode(),
                view.participantEventAvailabilityStatus(),
                view.participantEventStatus()
        );
    }

}
