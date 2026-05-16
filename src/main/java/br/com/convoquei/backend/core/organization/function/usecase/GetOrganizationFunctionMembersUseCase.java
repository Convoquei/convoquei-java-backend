package br.com.convoquei.backend.core.organization.function.usecase;

import br.com.convoquei.backend.app.api.dto.request.PagedRequest;
import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.core.organization.function.api.dto.response.OrganizationFunctionMemberResponse;
import br.com.convoquei.backend.core.organization.function.repository.OrganizationFunctionMemberRepository;
import br.com.convoquei.backend.core.organization.function.repository.OrganizationFunctionRepository;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetOrganizationFunctionMembersUseCase {

    private final OrganizationFunctionRepository organizationFunctionRepository;
    private final OrganizationFunctionMemberRepository organizationFunctionMemberRepository;

    public GetOrganizationFunctionMembersUseCase(
            OrganizationFunctionRepository organizationFunctionRepository,
            OrganizationFunctionMemberRepository organizationFunctionMemberRepository
    ) {
        this.organizationFunctionRepository = organizationFunctionRepository;
        this.organizationFunctionMemberRepository = organizationFunctionMemberRepository;
    }

    public PagedResponse<OrganizationFunctionMemberResponse> execute(UUID organizationId, UUID functionId, PagedRequest request) {
        requireFunction(organizationId, functionId);

        Sort sort = Sort.by("joinedAt").ascending();

        var page = organizationFunctionMemberRepository
                .findAllByOrganizationFunctionIdAndOrganizationId(functionId, organizationId, request.toPageRequest(sort))
                .map(OrganizationFunctionMemberResponse::buildFromFunctionMember);

        return PagedResponse.buildFromPage(page);
    }

    private void requireFunction(UUID organizationId, UUID functionId) {
        organizationFunctionRepository.findByIdAndOrganizationId(functionId, organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Função não encontrada nessa organização."));
    }
}
