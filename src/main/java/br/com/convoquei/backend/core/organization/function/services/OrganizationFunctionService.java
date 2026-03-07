package br.com.convoquei.backend.core.organization.function.services;

import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import br.com.convoquei.backend.app.api.dto.request.PagedRequest;
import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.organization.organization.repository.OrganizationRepository;
import br.com.convoquei.backend.core.organization.function.api.dto.request.AddFunctionMemberRequest;
import br.com.convoquei.backend.core.organization.function.api.dto.request.CreateFunctionRequest;
import br.com.convoquei.backend.core.organization.function.api.dto.request.ListFunctionsRequest;
import br.com.convoquei.backend.core.organization.function.api.dto.response.OrganizationFunctionMemberResponse;
import br.com.convoquei.backend.core.organization.function.api.dto.response.OrganizationFunctionResponse;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import br.com.convoquei.backend.core.organization.function.repository.OrganizationFunctionMemberRepository;
import br.com.convoquei.backend.core.organization.function.repository.OrganizationFunctionRepository;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.member.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.core.organization.member.repository.OrganizationMemberRepository;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;
import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrganizationFunctionService {

    private final OrganizationFunctionRepository organizationFunctionRepository;
    private final OrganizationFunctionMemberRepository organizationFunctionMemberRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRepository organizationRepository;
    private final CurrentUserProvider currentUserProvider;

    public OrganizationFunctionService(
            OrganizationFunctionRepository organizationFunctionRepository,
            OrganizationFunctionMemberRepository organizationFunctionMemberRepository,
            OrganizationMemberRepository organizationMemberRepository,
            OrganizationRepository organizationRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.organizationFunctionRepository = organizationFunctionRepository;
        this.organizationFunctionMemberRepository = organizationFunctionMemberRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationRepository = organizationRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public OrganizationFunctionResponse createFunction(UUID organizationId, CreateFunctionRequest request) {
        OrganizationMember requester = currentUserProvider.requireOrganizationMembership(organizationId);
        if (!requester.hasPermission(OrganizationPermission.MANAGE_FUNCTIONS))
            throw new DomainBusinessRuleException("Você não tem permissão para gerenciar funções desta organização.");

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Organização não encontrada."));

        OrganizationFunction function = new OrganizationFunction(organization, request.name(), request.emoji());
        organizationFunctionRepository.save(function);

        return OrganizationFunctionResponse.buildFromFunction(function);
    }

    public PagedResponse<OrganizationFunctionResponse> listFunctions(UUID organizationId, ListFunctionsRequest request) {
        currentUserProvider.requireOrganizationMembership(organizationId);

        Sort sort = Sort.by("createdAt").ascending();

        Page<OrganizationFunction> page;
        if (request.getName() != null && !request.getName().isBlank()) {
            page = organizationFunctionRepository.findAllByOrganizationIdAndNameContainingIgnoreCase(
                    organizationId, request.getName().trim(), request.toPageRequest(sort));
        } else {
            page = organizationFunctionRepository.findAllByOrganizationId(
                    organizationId, request.toPageRequest(sort));
        }

        return PagedResponse.buildFromPage(page.map(OrganizationFunctionResponse::buildFromFunction));
    }

    public PagedResponse<OrganizationFunctionMemberResponse> getFunctionMembers(UUID organizationId, UUID functionId, PagedRequest request) {
        currentUserProvider.requireOrganizationMembership(organizationId);

        requireFunction(organizationId, functionId);

        Sort sort = Sort.by("joinedAt").ascending();

        var page = organizationFunctionMemberRepository
                .findAllByOrganizationFunctionIdAndOrganizationId(functionId, organizationId, request.toPageRequest(sort))
                .map(OrganizationFunctionMemberResponse::buildFromFunctionMember);

        return PagedResponse.buildFromPage(page);
    }

    @Transactional
    public OrganizationFunctionResponse addMember(UUID organizationId, UUID functionId, AddFunctionMemberRequest request) {
        OrganizationMember requester = currentUserProvider.requireOrganizationMembership(organizationId);
        if (!requester.hasPermission(OrganizationPermission.MANAGE_FUNCTIONS))
            throw new DomainBusinessRuleException("Você não tem permissão para gerenciar funções desta organização.");

        OrganizationFunction function = requireFunction(organizationId, functionId);

        OrganizationMember memberToAdd = organizationMemberRepository
                .findByIdAndOrganizationIdAndStatus(request.memberId(), organizationId, OrganizationMemberStatus.ACTIVE)
                .orElseThrow(() -> new DomainNotFoundException("Membro não encontrado nessa organização."));

        function.addMember(memberToAdd);
        organizationFunctionRepository.save(function);

        return OrganizationFunctionResponse.buildFromFunction(function);
    }

    @Transactional
    public OrganizationFunctionResponse removeMember(UUID organizationId, UUID functionId, UUID memberId) {
        OrganizationMember requester = currentUserProvider.requireOrganizationMembership(organizationId);
        if (!requester.hasPermission(OrganizationPermission.MANAGE_FUNCTIONS))
            throw new DomainBusinessRuleException("Você não tem permissão para gerenciar funções desta organização.");

        OrganizationFunction function = requireFunction(organizationId, functionId);

        function.removeMember(memberId);
        organizationFunctionRepository.save(function);

        return OrganizationFunctionResponse.buildFromFunction(function);
    }

    @Transactional
    public OrganizationFunctionResponse clearFunctionMembers(UUID organizationId, UUID functionId) {
        OrganizationMember requester = currentUserProvider.requireOrganizationMembership(organizationId);
        if (!requester.hasPermission(OrganizationPermission.MANAGE_FUNCTIONS))
            throw new DomainBusinessRuleException("Você não tem permissão para gerenciar funções desta organização.");

        OrganizationFunction function = requireFunction(organizationId, functionId);

        function.clearMembers();
        organizationFunctionRepository.save(function);

        return OrganizationFunctionResponse.buildFromFunction(function);
    }

    @Transactional
    public void deleteFunction(UUID organizationId, UUID functionId) {
        OrganizationMember requester = currentUserProvider.requireOrganizationMembership(organizationId);
        if (!requester.hasPermission(OrganizationPermission.MANAGE_FUNCTIONS))
            throw new DomainBusinessRuleException("Você não tem permissão para gerenciar funções desta organização.");

        OrganizationFunction function = requireFunction(organizationId, functionId);

        organizationFunctionRepository.delete(function);
    }

    private OrganizationFunction requireFunction(UUID organizationId, UUID functionId) {
        return organizationFunctionRepository.findByIdAndOrganizationId(functionId, organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Função não encontrada nessa organização."));
    }
}
