package br.com.convoquei.backend.core.organization.event.services;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.repository.OrganizationEventRepository;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CancelOrganizationEventService {
    private final CurrentUserProvider currentUserProvider;
    private final OrganizationEventRepository organizationEventRepository;

    public CancelOrganizationEventService(CurrentUserProvider currentUserProvider, OrganizationEventRepository organizationEventRepository) {
        this.currentUserProvider = currentUserProvider;
        this.organizationEventRepository = organizationEventRepository;
    }

    @Transactional
    public void execute(UUID organizationId, UUID eventId) {
        OrganizationMember member = currentUserProvider.requireOrganizationMembership(organizationId);

        OrganizationEvent event = organizationEventRepository
                .findByIdAndOrganizationId(eventId, organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Evento não encontrado nesta organização."));

        event.cancel(member);

        organizationEventRepository.save(event);
    }
}
