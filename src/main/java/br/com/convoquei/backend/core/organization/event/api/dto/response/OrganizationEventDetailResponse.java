package br.com.convoquei.backend.core.organization.event.api.dto.response;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEventCalledFunction;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEventCalledGroup;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEventParticipant;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentMode;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventCallMode;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventFunctionCallMode;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantAvailabilityStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;
import br.com.convoquei.backend.core.organization.event.valueobject.OrganizationEventConfiguration;
import br.com.convoquei.backend.core.organization.event.util.OrganizationEventHelper;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrganizationEventDetailResponse(
        UUID id,
        String name,
        String description,
        String location,
        OrganizationEventStatus status,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        OrganizationEventAssignmentMode assignmentMode,
        OrganizationEventCallMode callMode,
        OrganizationEventFunctionCallMode functionCallMode,
        int availabilityLockBeforeStartInMinutes,
        long availabilityLockEpochSeconds,
        boolean allowAssignment,
        List<OrganizationEventDetailCalledGroupResponse> calledGroups,
        List<OrganizationEventDetailCalledFunctionResponse> calledFunctions,
        List<OrganizationEventDetailParticipantResponse> participants
) {

    public record OrganizationEventDetailCalledGroupResponse(
            UUID id,
            String name,
            String icon
    ) {
        public static OrganizationEventDetailCalledGroupResponse from(OrganizationEventCalledGroup calledGroup) {
            return new OrganizationEventDetailCalledGroupResponse(
                    calledGroup.getGroup().getId(),
                    calledGroup.getGroup().getName(),
                    calledGroup.getGroup().getEmoji()
            );
        }
    }

    public record OrganizationEventDetailCalledFunctionResponse(
            UUID functionId,
            String functionName,
            String icon,
            int minMembers,
            Integer maxMembers,
            OrganizationEventAssignmentMode assignmentMode
    ) {
        public static OrganizationEventDetailCalledFunctionResponse from(OrganizationEventCalledFunction calledFunction) {
            return new OrganizationEventDetailCalledFunctionResponse(
                    calledFunction.getFunction().getId(),
                    calledFunction.getFunction().getName(),
                    calledFunction.getFunction().getEmoji(),
                    calledFunction.getMinMembers(),
                    calledFunction.getMaxMembers(),
                    calledFunction.getAssignmentMode()
            );
        }
    }

    public record OrganizationEventDetailParticipantResponse(
            UUID membershipId,
            String name,
            String avatarUrl,
            String function,
            OrganizationEventParticipantStatus status,
            OrganizationEventParticipantAvailabilityStatus availabilityStatus,
            Instant updatedAt
    ) {
        public static OrganizationEventDetailParticipantResponse from(OrganizationEventParticipant participant) {
            OrganizationFunction function = participant.getFunction();
            return new OrganizationEventDetailParticipantResponse(
                    participant.getId(),
                    participant.getDisplayNameSnapshot(),
                    participant.getAvatarUrlSnapshot(),
                    function != null ? function.getName() : null,
                    participant.getStatus(),
                    participant.getAvailabilityStatus(),
                    participant.getUpdatedAt()
            );
        }
    }

    public static OrganizationEventDetailResponse from(OrganizationEvent event) {
        OrganizationEventConfiguration config = event.getConfiguration();

        List<OrganizationEventDetailCalledGroupResponse> calledGroups = event.getCalledGroups().stream()
                .map(OrganizationEventDetailCalledGroupResponse::from)
                .toList();

        List<OrganizationEventDetailCalledFunctionResponse> calledFunctions = event.getCalledFunctions().stream()
                .map(OrganizationEventDetailCalledFunctionResponse::from)
                .toList();

        List<OrganizationEventDetailParticipantResponse> participants = event.getParticipants().stream()
                .map(OrganizationEventDetailParticipantResponse::from)
                .toList();

        return new OrganizationEventDetailResponse(
                event.getId(),
                config.getName(),
                config.getDescription(),
                config.getLocation(),
                event.getStatus(),
                event.getStartAt(),
                event.getEndAt(),
                config.getAssignmentMode(),
                config.getCallMode(),
                config.getFunctionCallMode(),
                config.getAvailabilityLockBeforeStartInMinutes(),
                OrganizationEventHelper.secondsUntilAvailabilityLock(event.getStartAt(), config.getAvailabilityLockBeforeStartInMinutes()),
                event.isAllowAssignments(),
                calledGroups,
                calledFunctions,
                participants
        );
    }
}
