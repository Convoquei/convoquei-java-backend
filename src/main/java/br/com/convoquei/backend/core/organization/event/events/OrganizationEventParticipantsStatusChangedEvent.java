package br.com.convoquei.backend.core.organization.event.events;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEventParticipant;

import java.util.List;

public record OrganizationEventParticipantsStatusChangedEvent(List<OrganizationEventParticipant> participants) {
}
