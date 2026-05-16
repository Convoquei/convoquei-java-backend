package br.com.convoquei.backend.core.organization.function.usecase;

import br.com.convoquei.backend.core.organization.event.services.SyncEventParticipantsService;
import br.com.convoquei.backend.core.organization.function.api.dto.response.OrganizationFunctionResponse;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunctionMember;
import br.com.convoquei.backend.core.organization.function.repository.OrganizationFunctionRepository;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RemoveOrganizationFunctionMemberUseCase {

    private final OrganizationFunctionRepository organizationFunctionRepository;
    private final SyncEventParticipantsService syncEventParticipantsService;

    public RemoveOrganizationFunctionMemberUseCase(
            OrganizationFunctionRepository organizationFunctionRepository,
            SyncEventParticipantsService syncEventParticipantsService
    ) {
        this.organizationFunctionRepository = organizationFunctionRepository;
        this.syncEventParticipantsService = syncEventParticipantsService;
    }

    @Transactional
    public OrganizationFunctionResponse execute(UUID organizationId, UUID functionId, UUID functionParticipantId) {
        OrganizationFunction function = organizationFunctionRepository.requireByIdAndOrganizationId(functionId, organizationId);

        OrganizationMember removedMember = function.getMembers().stream()
                .filter(m -> m.getId().equals(functionParticipantId))
                .map(OrganizationFunctionMember::getOrganizationMember)
                .findFirst()
                .orElseThrow(() -> new DomainNotFoundException("Membro não encontrado nesta função."));

        function.removeMember(functionParticipantId);
        organizationFunctionRepository.save(function);
        syncEventParticipantsService.executeForMember(organizationId, removedMember);

        return OrganizationFunctionResponse.buildFromFunction(function);
    }
}
