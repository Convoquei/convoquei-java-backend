package br.com.convoquei.backend.core.organization.event.repository.views;

import br.com.convoquei.backend.core.organization.event.enums.*;

import java.time.OffsetDateTime;
import java.util.List;
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
        OrganizationEventFunctionCallMode functionCallMode,
        OrganizationEventAssignmentMode assignmentMode,
        OrganizationEventParticipantAvailabilityStatus participantEventAvailabilityStatus,
        OrganizationEventParticipantStatus participantEventStatus,
        UUID membershipFunctionId,
        String membershipFunctionName,
        String membershipFunctionEmoji,
        String cancelledByName,
        List<OrganizationEventCalledFunctionView> calledFunctions
) {
    public OrganizationEventMemberView(
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
            OrganizationEventFunctionCallMode functionCallMode,
            OrganizationEventAssignmentMode assignmentMode,
            OrganizationEventParticipantAvailabilityStatus participantEventAvailabilityStatus,
            OrganizationEventParticipantStatus participantEventStatus,
            UUID membershipFunctionId,
            String membershipFunctionName,
            String membershipFunctionEmoji,
            String cancelledByName
    ) {
        this(
                id, name, description, location, startAt, endAt, status,
                availabilityLockBeforeStartInMinutes, allowAssignments, participantsCount,
                functionCallMode, assignmentMode, participantEventAvailabilityStatus,
                participantEventStatus, membershipFunctionId, membershipFunctionName,
                membershipFunctionEmoji, cancelledByName, List.of()
        );
    }

    public OrganizationEventMemberView withCalledFunctions(List<OrganizationEventCalledFunctionView> calledFunctions) {
        return new OrganizationEventMemberView(
                id, name, description, location, startAt, endAt, status,
                availabilityLockBeforeStartInMinutes, allowAssignments, participantsCount,
                functionCallMode, assignmentMode, participantEventAvailabilityStatus,
                participantEventStatus, membershipFunctionId, membershipFunctionName,
                membershipFunctionEmoji, cancelledByName, calledFunctions
        );
    }
}