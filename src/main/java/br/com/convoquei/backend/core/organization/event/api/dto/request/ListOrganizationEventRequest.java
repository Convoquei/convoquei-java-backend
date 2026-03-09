package br.com.convoquei.backend.core.organization.event.api.dto.request;

import br.com.convoquei.backend.app.api.dto.request.PagedRequest;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;

import java.time.OffsetDateTime;
import java.util.List;

public class ListOrganizationEventRequest extends PagedRequest {
    private String name;
    private OffsetDateTime startAfter;
    private OffsetDateTime startBefore;
    private List<OrganizationEventParticipantStatus> participantStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getStartAfter() {
        return startAfter;
    }

    public OffsetDateTime getStartBefore() {
        return startBefore;
    }

    public List<OrganizationEventParticipantStatus> getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(List<OrganizationEventParticipantStatus> participantStatus) {
        this.participantStatus = participantStatus;
    }

    public void setStartAfter(OffsetDateTime startAfter) {
        this.startAfter = startAfter;
    }

    public void setStartBefore(OffsetDateTime startBefore) {
        this.startBefore = startBefore;
    }
}
