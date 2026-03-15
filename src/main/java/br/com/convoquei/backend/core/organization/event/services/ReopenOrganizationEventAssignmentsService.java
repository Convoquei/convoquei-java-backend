package br.com.convoquei.backend.core.organization.event.services;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.repository.OrganizationEventRepository;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReopenOrganizationEventAssignmentsService {

    private final OrganizationEventRepository organizationEventRepository;

    public ReopenOrganizationEventAssignmentsService(OrganizationEventRepository organizationEventRepository) {
        this.organizationEventRepository = organizationEventRepository;
    }

    @Transactional
    public void execute(UUID organizationId, UUID eventId) {
        OrganizationEvent event = organizationEventRepository
                .findByIdAndOrganizationId(eventId, organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Evento não encontrado nesta organização."));

        event.reopenAssignments();

        organizationEventRepository.save(event);
    }
}
