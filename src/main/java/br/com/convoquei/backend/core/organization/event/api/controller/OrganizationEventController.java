package br.com.convoquei.backend.core.organization.event.api.controller;

import br.com.convoquei.backend.app.security.annotation.RequiredOrganizationPermission;
import br.com.convoquei.backend.core.organization.event.api.dto.request.CreateOrganizationEventRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.response.OrganizationEventResponse;
import br.com.convoquei.backend.core.organization.event.services.CreateOrganizationEventService;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/events")
public class OrganizationEventController {

    public final CreateOrganizationEventService createOrganizationService;

    public OrganizationEventController(CreateOrganizationEventService createOrganizationService) {
        this.createOrganizationService = createOrganizationService;
    }

    @PostMapping
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_EVENTS)
    public OrganizationEventResponse createEvent(@PathVariable UUID organizationId, @RequestBody @Valid CreateOrganizationEventRequest request) {
        return createOrganizationService.execute(organizationId, request);
    }
}
