package br.com.convoquei.backend.core.organization.event.api.controller;

import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.app.security.annotation.RequiredOrganizationPermission;
import br.com.convoquei.backend.core.organization.event.api.dto.request.CreateCreateOrganizationEventRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.request.UpdateOrganizationEventRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.request.ListOrganizationEventRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.request.RespondEventAvailabilityRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.response.OrganizationEventDetailResponse;
import br.com.convoquei.backend.core.organization.event.api.dto.response.OrganizationEventMembershipResponse;
import br.com.convoquei.backend.core.organization.event.services.*;
import br.com.convoquei.backend.core.organization.event.services.LockOrganizationEventAssignmentsService;
import br.com.convoquei.backend.core.organization.event.services.ReopenOrganizationEventAssignmentsService;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/events")
public class OrganizationEventController {

    private final CreateOrganizationEventService createOrganizationService;
    private final CancelOrganizationEventService cancelOrganizationEventService;
    private final OrganizationEventMembershipService organizationEventMembershipService;
    private final RespondEventAvailabilityService respondEventAvailabilityService;
    private final ClearEventAvailabilityService clearEventAvailabilityService;
    private final GetOrganizationEventDetailService getOrganizationEventDetailService;
    private final UpdateOrganizationEventService updateOrganizationEventService;
    private final ReopenOrganizationEventAssignmentsService reopenOrganizationEventAssignmentsService;
    private final LockOrganizationEventAssignmentsService lockOrganizationEventAssignmentsService;
    private final ApproveParticipantAssignmentService approveParticipantAssignmentService;
    private final RemoveParticipantAssignmentService removeParticipantAssignmentService;
    private final AssignParticipantService assignParticipantService;

    public OrganizationEventController(CreateOrganizationEventService createOrganizationService, CancelOrganizationEventService cancelOrganizationEventService, OrganizationEventMembershipService organizationEventMembershipService, RespondEventAvailabilityService respondEventAvailabilityService, ClearEventAvailabilityService clearEventAvailabilityService, GetOrganizationEventDetailService getOrganizationEventDetailService, UpdateOrganizationEventService updateOrganizationEventService, ReopenOrganizationEventAssignmentsService reopenOrganizationEventAssignmentsService, LockOrganizationEventAssignmentsService lockOrganizationEventAssignmentsService, ApproveParticipantAssignmentService approveParticipantAssignmentService, RemoveParticipantAssignmentService removeParticipantAssignmentService, AssignParticipantService assignParticipantService) {
        this.createOrganizationService = createOrganizationService;
        this.cancelOrganizationEventService = cancelOrganizationEventService;
        this.organizationEventMembershipService = organizationEventMembershipService;
        this.respondEventAvailabilityService = respondEventAvailabilityService;
        this.clearEventAvailabilityService = clearEventAvailabilityService;
        this.getOrganizationEventDetailService = getOrganizationEventDetailService;
        this.updateOrganizationEventService = updateOrganizationEventService;
        this.reopenOrganizationEventAssignmentsService = reopenOrganizationEventAssignmentsService;
        this.lockOrganizationEventAssignmentsService = lockOrganizationEventAssignmentsService;
        this.approveParticipantAssignmentService = approveParticipantAssignmentService;
        this.removeParticipantAssignmentService = removeParticipantAssignmentService;
        this.assignParticipantService = assignParticipantService;
    }

    @PostMapping
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_EVENTS)
    public OrganizationEventMembershipResponse createEvent(@PathVariable UUID organizationId, @RequestBody @Valid CreateCreateOrganizationEventRequest request) {
        return createOrganizationService.execute(organizationId, request);
    }

    @PostMapping("/{eventId}/cancel")
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_EVENTS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelEvent(@PathVariable UUID organizationId, @PathVariable UUID eventId) {
        cancelOrganizationEventService.execute(organizationId, eventId);
    }

    @GetMapping("/{eventId}")
    @RequiredOrganizationPermission(OrganizationPermission.MEMBER)
    public OrganizationEventMembershipResponse getEvent(
            @PathVariable UUID organizationId,
            @PathVariable UUID eventId) {
        return organizationEventMembershipService.getEvent(organizationId, eventId);
    }

    @GetMapping
    @RequiredOrganizationPermission(OrganizationPermission.MEMBER)
    public PagedResponse<OrganizationEventMembershipResponse> listEvents(@PathVariable UUID organizationId, @Valid @ParameterObject ListOrganizationEventRequest request) {
        return organizationEventMembershipService.listEvents(organizationId, request);
    }

    @GetMapping("/{eventId}/details")
    @RequiredOrganizationPermission(OrganizationPermission.MEMBER)
    public OrganizationEventDetailResponse getEventDetail(
            @PathVariable UUID organizationId,
            @PathVariable UUID eventId) {
        return getOrganizationEventDetailService.execute(organizationId, eventId);
    }

    @PatchMapping("/{eventId}")
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_EVENTS)
    public OrganizationEventDetailResponse updateEvent(
            @PathVariable UUID organizationId,
            @PathVariable UUID eventId,
            @RequestBody @Valid UpdateOrganizationEventRequest request) {
        return updateOrganizationEventService.execute(organizationId, eventId, request);
    }

    @PatchMapping("/{eventId}/availability")
    @RequiredOrganizationPermission(OrganizationPermission.MEMBER)
    public OrganizationEventMembershipResponse respondAvailability(
            @PathVariable UUID organizationId,
            @PathVariable UUID eventId,
            @RequestBody @Valid RespondEventAvailabilityRequest request) {
        return respondEventAvailabilityService.execute(organizationId, eventId, request);
    }

    @DeleteMapping("/{eventId}/availability")
    @RequiredOrganizationPermission(OrganizationPermission.MEMBER)
    public OrganizationEventMembershipResponse clearAvailability(
            @PathVariable UUID organizationId,
            @PathVariable UUID eventId) {
        return clearEventAvailabilityService.execute(organizationId, eventId);
    }

    @PostMapping("/{eventId}/reopen-assignments")
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_EVENTS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reopenAssignments(
            @PathVariable UUID organizationId,
            @PathVariable UUID eventId) {
        reopenOrganizationEventAssignmentsService.execute(organizationId, eventId);
    }

    @PostMapping("/{eventId}/lock-assignments")
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_EVENTS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void lockAssignments(
            @PathVariable UUID organizationId,
            @PathVariable UUID eventId) {
        lockOrganizationEventAssignmentsService.execute(organizationId, eventId);
    }

    @PostMapping("/{eventId}/participants/{participantId}/assign")
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_EVENT_SCHEDULE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignParticipant(
            @PathVariable UUID organizationId,
            @PathVariable UUID eventId,
            @PathVariable UUID participantId) {
        assignParticipantService.execute(organizationId, eventId, participantId);
    }

    @PostMapping("/{eventId}/participants/{participantId}/assignment/approve")
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_EVENT_SCHEDULE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveParticipantAssignment(
            @PathVariable UUID organizationId,
            @PathVariable UUID eventId,
            @PathVariable UUID participantId) {
        approveParticipantAssignmentService.execute(organizationId, eventId, participantId);
    }

    @DeleteMapping("/{eventId}/participants/{participantId}/assignment")
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_EVENT_SCHEDULE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeParticipantAssignment(
            @PathVariable UUID organizationId,
            @PathVariable UUID eventId,
            @PathVariable UUID participantId) {
        removeParticipantAssignmentService.execute(organizationId, eventId, participantId);
    }
}
