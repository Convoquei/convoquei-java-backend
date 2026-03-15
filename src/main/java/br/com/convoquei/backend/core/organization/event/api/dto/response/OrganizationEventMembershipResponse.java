package br.com.convoquei.backend.core.organization.event.api.dto.response;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEventParticipant;
import br.com.convoquei.backend.core.organization.event.enums.*;
import br.com.convoquei.backend.core.organization.event.repository.views.OrganizationEventMemberView;
import br.com.convoquei.backend.core.organization.event.valueobject.OrganizationEventConfiguration;
import br.com.convoquei.backend.core.organization.event.util.OrganizationEventHelper;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;

import java.time.OffsetDateTime;
import java.util.List;
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
        OrganizationEventFunctionCallMode functionCallMode,
        OrganizationEventAssignmentMode assignmentMode,
        OrganizationEventParticipantAvailabilityStatus membershipAvailabilityStatus,
        OrganizationEventParticipantStatus membershipStatus,
        UUID membershipFunctionId,
        String membershipFunctionName,
        String membershipFunctionEmoji,
        List<EventCalledFunctionResponse> calledFunctions,
        String cancelledByName
) {
    public static OrganizationEventMembershipResponse buildFromEventAndMembership(OrganizationEvent event, UUID memberId) {
        OrganizationEventConfiguration configuration = event.getConfiguration();

        Optional<OrganizationEventParticipant> membership = event.findParticipantByMemberId(memberId);

        OrganizationFunction memberFunction = membership
                .map(OrganizationEventParticipant::getFunction)
                .orElse(null);

        List<EventCalledFunctionResponse> calledFunctions = event.getCalledFunctions().stream()
                .map(EventCalledFunctionResponse::from)
                .toList();

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
                event.getConfiguration().getFunctionCallMode(),
                configuration.getAssignmentMode(),
                membership.map(OrganizationEventParticipant::getAvailabilityStatus).orElse(null),
                membership.map(OrganizationEventParticipant::getStatus).orElse(null),
                memberFunction != null ? memberFunction.getId() : null,
                memberFunction != null ? memberFunction.getName() : null,
                memberFunction != null ? memberFunction.getEmoji() : null,
                calledFunctions,
                event.getCancelledBy() != null ? event.getCancelledBy().getUser().getName() : null
        );
    }

    public static OrganizationEventMembershipResponse buildFromEventMembershipView(OrganizationEventMemberView view) {

        int availabilityLockBeforeStartInMinutes = view.availabilityLockBeforeStartInMinutes() != null
                ? view.availabilityLockBeforeStartInMinutes()
                : 0;

        long secondsUntilLock = OrganizationEventHelper.secondsUntilAvailabilityLock(view.startAt(), view.availabilityLockBeforeStartInMinutes());

        return new OrganizationEventMembershipResponse(
                view.id(),
                view.name(),
                view.description(),
                view.location(),
                view.startAt(),
                view.endAt(),
                view.status(),
                availabilityLockBeforeStartInMinutes,
                secondsUntilLock,
                view.allowAssignments(),
                Math.toIntExact(view.participantsCount()),
                view.functionCallMode(),
                view.assignmentMode(),
                view.participantEventAvailabilityStatus(),
                view.participantEventStatus(),
                view.membershipFunctionId(),
                view.membershipFunctionName(),
                view.membershipFunctionEmoji(),
                view.calledFunctions().stream().map(EventCalledFunctionResponse::from).toList(),
                view.cancelledByName()
        );
    }

}
