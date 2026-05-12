package br.com.convoquei.backend.core.organization.event.services;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.repository.OrganizationEventRepository;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ApproveParticipantAssignmentService {

    private final OrganizationEventRepository organizationEventRepository;

    public ApproveParticipantAssignmentService(OrganizationEventRepository organizationEventRepository) {
        this.organizationEventRepository = organizationEventRepository;
    }

    @Transactional
    public void execute(UUID organizationId, UUID eventId, UUID participantId) {
        OrganizationEvent event = organizationEventRepository
                .findFullDetailByIdAndOrganizationId(eventId, organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Evento não encontrado nesta organização."));

        event.approveParticipantAssignment(participantId);

        organizationEventRepository.save(event);
    }
}
