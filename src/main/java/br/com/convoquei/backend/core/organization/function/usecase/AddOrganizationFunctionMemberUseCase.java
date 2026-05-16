package br.com.convoquei.backend.core.organization.function.usecase;

import br.com.convoquei.backend.core.organization.event.services.SyncEventParticipantsService;
import br.com.convoquei.backend.core.organization.function.api.dto.request.AddFunctionMemberRequest;
import br.com.convoquei.backend.core.organization.function.api.dto.response.OrganizationFunctionResponse;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import br.com.convoquei.backend.core.organization.function.repository.OrganizationFunctionRepository;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.member.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.core.organization.member.repository.OrganizationMemberRepository;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AddOrganizationFunctionMemberUseCase {

    private final OrganizationFunctionRepository organizationFunctionRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final SyncEventParticipantsService syncEventParticipantsService;

    public AddOrganizationFunctionMemberUseCase(
            OrganizationFunctionRepository organizationFunctionRepository,
            OrganizationMemberRepository organizationMemberRepository,
            SyncEventParticipantsService syncEventParticipantsService
    ) {
        this.organizationFunctionRepository = organizationFunctionRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.syncEventParticipantsService = syncEventParticipantsService;
    }

    @Transactional
    public OrganizationFunctionResponse execute(UUID organizationId, UUID functionId, AddFunctionMemberRequest request) {
        OrganizationFunction function = organizationFunctionRepository.requireByIdAndOrganizationId(functionId, organizationId);

        OrganizationMember memberToAdd = organizationMemberRepository
                .findByIdAndOrganizationIdAndStatus(request.memberId(), organizationId, OrganizationMemberStatus.ACTIVE)
                .orElseThrow(() -> new DomainNotFoundException("Membro não encontrado nessa organização."));

        function.addMember(memberToAdd);
        organizationFunctionRepository.save(function);
        syncEventParticipantsService.executeForMember(organizationId, memberToAdd);

        return OrganizationFunctionResponse.buildFromFunction(function);
    }
}
