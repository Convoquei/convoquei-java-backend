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

import java.util.List;
import java.util.UUID;

@Service
public class ClearOrganizationFunctionMembersUseCase {

    private final OrganizationFunctionRepository organizationFunctionRepository;
    private final SyncEventParticipantsService syncEventParticipantsService;

    public ClearOrganizationFunctionMembersUseCase(
            OrganizationFunctionRepository organizationFunctionRepository,
            SyncEventParticipantsService syncEventParticipantsService
    ) {
        this.organizationFunctionRepository = organizationFunctionRepository;
        this.syncEventParticipantsService = syncEventParticipantsService;
    }

    @Transactional
    public OrganizationFunctionResponse execute(UUID organizationId, UUID functionId) {
        OrganizationFunction function = organizationFunctionRepository.requireByIdAndOrganizationId(functionId, organizationId);

        List<OrganizationMember> removedMembers = function.getMembers().stream()
                .map(OrganizationFunctionMember::getOrganizationMember)
                .toList();

        function.clearMembers();
        organizationFunctionRepository.save(function);
        syncEventParticipantsService.executeForMembers(organizationId, removedMembers);

        return OrganizationFunctionResponse.buildFromFunction(function);
    }
}
