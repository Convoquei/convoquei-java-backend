package br.com.convoquei.backend.core.organization.event.api.dto.request;

import br.com.convoquei.backend.app.api.dto.request.PagedRequest;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantAvailabilityStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;

import java.time.OffsetDateTime;
import java.util.List;

public class ListOrganizationEventRequest extends PagedRequest {
    private String name;
    private OffsetDateTime startAfter;
    private OffsetDateTime startBefore;
    private List<OrganizationEventParticipantAvailabilityStatus> availabilityStatus;
    private List<OrganizationEventParticipantStatus> participantStatus;
    private List<OrganizationEventStatus> eventStatus;

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

    public List<OrganizationEventParticipantAvailabilityStatus> getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(List<OrganizationEventParticipantAvailabilityStatus> availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
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

    public List<OrganizationEventStatus> getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(List<OrganizationEventStatus> eventStatus) {
        this.eventStatus = eventStatus;
    }
}
