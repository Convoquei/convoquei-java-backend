package br.com.convoquei.backend.core.organization.event.api.dto.response;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;

public record OrganizationEventResponse() {
    public static OrganizationEventResponse buildFromEvent(OrganizationEvent event) {
        return new OrganizationEventResponse();
    }
}
