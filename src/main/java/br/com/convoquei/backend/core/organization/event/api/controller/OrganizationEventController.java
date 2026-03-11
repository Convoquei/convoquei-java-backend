package br.com.convoquei.backend.core.organization.event.api.controller;

import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.app.security.annotation.RequiredOrganizationPermission;
import br.com.convoquei.backend.core.organization.event.api.dto.request.CreateCreateOrganizationEventRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.request.ListOrganizationEventRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.request.RespondEventAvailabilityRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.response.OrganizationEventDetailResponse;
import br.com.convoquei.backend.core.organization.event.api.dto.response.OrganizationEventMembershipResponse;
import br.com.convoquei.backend.core.organization.event.services.ClearEventAvailabilityService;
import br.com.convoquei.backend.core.organization.event.services.GetOrganizationEventDetailService;
import br.com.convoquei.backend.core.organization.event.services.CreateOrganizationEventService;
import br.com.convoquei.backend.core.organization.event.services.OrganizationEventMembershipService;
import br.com.convoquei.backend.core.organization.event.services.RespondEventAvailabilityService;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/events")
public class OrganizationEventController {

    public final CreateOrganizationEventService createOrganizationService;
    public final OrganizationEventMembershipService organizationEventMembershipService;
    public final RespondEventAvailabilityService respondEventAvailabilityService;
    public final ClearEventAvailabilityService clearEventAvailabilityService;
    public final GetOrganizationEventDetailService getOrganizationEventDetailService;

    public OrganizationEventController(CreateOrganizationEventService createOrganizationService, OrganizationEventMembershipService organizationEventMembershipService, RespondEventAvailabilityService respondEventAvailabilityService, ClearEventAvailabilityService clearEventAvailabilityService, GetOrganizationEventDetailService getOrganizationEventDetailService) {
        this.createOrganizationService = createOrganizationService;
        this.organizationEventMembershipService = organizationEventMembershipService;
        this.respondEventAvailabilityService = respondEventAvailabilityService;
        this.clearEventAvailabilityService = clearEventAvailabilityService;
        this.getOrganizationEventDetailService = getOrganizationEventDetailService;
    }

    @PostMapping
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_EVENTS)
    public OrganizationEventMembershipResponse createEvent(@PathVariable UUID organizationId, @RequestBody @Valid CreateCreateOrganizationEventRequest request) {
        return createOrganizationService.execute(organizationId, request);
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
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_EVENTS)
    public OrganizationEventDetailResponse getEventDetail(
            @PathVariable UUID organizationId,
            @PathVariable UUID eventId) {
        return getOrganizationEventDetailService.execute(organizationId, eventId);
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
}
