package br.com.convoquei.backend.core.organization.event.services;

import br.com.convoquei.backend.core.organization.event.api.dto.request.RespondEventAvailabilityRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.response.OrganizationEventMembershipResponse;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEventParticipant;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentMode;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantAvailabilityStatus;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import br.com.convoquei.backend.core.organization.event.repository.OrganizationEventRepository;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class RespondEventAvailabilityService {

    private final OrganizationEventRepository organizationEventRepository;
    private final CurrentUserProvider currentUserProvider;

    public RespondEventAvailabilityService(OrganizationEventRepository organizationEventRepository, CurrentUserProvider currentUserProvider) {
        this.organizationEventRepository = organizationEventRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public OrganizationEventMembershipResponse execute(UUID organizationId, UUID eventId, RespondEventAvailabilityRequest request) {
        OrganizationMember member = currentUserProvider.requireOrganizationMembership(organizationId);

        OrganizationEvent event = organizationEventRepository
                .findByIdAndOrganizationId(eventId, organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Evento não encontrado nesta organização."));

        event.respondAvailability(member.getId(), request.available());

        return OrganizationEventMembershipResponse.buildFromEventAndMembership(event, member.getId());
    }
}

