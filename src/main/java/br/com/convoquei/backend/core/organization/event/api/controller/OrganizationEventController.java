package br.com.convoquei.backend.core.organization.event.api.controller;

import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.app.security.annotation.RequiredOrganizationPermission;
import br.com.convoquei.backend.core.organization.event.api.dto.request.CreateCreateOrganizationEventRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.request.ListOrganizationEventRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.response.OrganizationEventMembershipResponse;
import br.com.convoquei.backend.core.organization.event.services.CreateOrganizationEventService;
import br.com.convoquei.backend.core.organization.event.services.OrganizationEventMembershipService;
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


    public OrganizationEventController(CreateOrganizationEventService createOrganizationService, OrganizationEventMembershipService organizationEventMembershipService) {
        this.createOrganizationService = createOrganizationService;
        this.organizationEventMembershipService = organizationEventMembershipService;
    }

    @PostMapping
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_EVENTS)
    public OrganizationEventMembershipResponse createEvent(@PathVariable UUID organizationId, @RequestBody @Valid CreateCreateOrganizationEventRequest request) {
        return createOrganizationService.execute(organizationId, request);
    }

    @GetMapping
    @RequiredOrganizationPermission(OrganizationPermission.MEMBER)
    public PagedResponse<OrganizationEventMembershipResponse> listEvents(@PathVariable UUID organizationId, @Valid @ParameterObject ListOrganizationEventRequest request) {
        return organizationEventMembershipService.listEvents(organizationId, request);
    }
}
