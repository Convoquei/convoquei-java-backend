package br.com.convoquei.backend.core.organization.group.services;

import br.com.convoquei.backend.core.organization.event.services.SyncEventParticipantsService;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import br.com.convoquei.backend.app.api.dto.request.PagedRequest;
import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.organization.organization.repository.OrganizationRepository;
import br.com.convoquei.backend.core.organization.group.api.dto.request.AddGroupParticipantRequest;
import br.com.convoquei.backend.core.organization.group.api.dto.request.CreateGroupRequest;
import br.com.convoquei.backend.core.organization.group.api.dto.request.ListGroupsRequest;
import br.com.convoquei.backend.core.organization.group.api.dto.response.OrganizationGroupParticipantResponse;
import br.com.convoquei.backend.core.organization.group.api.dto.response.OrganizationGroupResponse;
import br.com.convoquei.backend.core.organization.group.entity.OrganizationGroup;
import br.com.convoquei.backend.core.organization.group.entity.OrganizationGroupParticipant;
import br.com.convoquei.backend.core.organization.group.repository.OrganizationGroupParticipantRepository;
import br.com.convoquei.backend.core.organization.group.repository.OrganizationGroupRepository;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.member.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.core.organization.member.repository.OrganizationMemberRepository;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;
import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationGroupService {

    private final OrganizationGroupRepository organizationGroupRepository;
    private final OrganizationGroupParticipantRepository organizationGroupParticipantRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRepository organizationRepository;
    private final CurrentUserProvider currentUserProvider;
    private final SyncEventParticipantsService syncEventParticipantsService;

    public OrganizationGroupService(
            OrganizationGroupRepository organizationGroupRepository,
            OrganizationGroupParticipantRepository organizationGroupParticipantRepository,
            OrganizationMemberRepository organizationMemberRepository,
            OrganizationRepository organizationRepository,
            CurrentUserProvider currentUserProvider,
            SyncEventParticipantsService syncEventParticipantsService
    ) {
        this.organizationGroupRepository = organizationGroupRepository;
        this.organizationGroupParticipantRepository = organizationGroupParticipantRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationRepository = organizationRepository;
        this.currentUserProvider = currentUserProvider;
        this.syncEventParticipantsService = syncEventParticipantsService;
    }

    @Transactional
    public OrganizationGroupResponse createGroup(UUID organizationId, CreateGroupRequest request) {
        OrganizationMember requester = currentUserProvider.requireOrganizationMembership(organizationId);
        if (!requester.hasPermission(OrganizationPermission.MANAGE_GROUPS))
            throw new DomainBusinessRuleException("Você não tem permissão para gerenciar grupos desta organização.");

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Organização não encontrada."));

        OrganizationGroup group = new OrganizationGroup(organization, request.name(), request.emoji());
        organizationGroupRepository.save(group);

        return OrganizationGroupResponse.buildFromGroup(group);
    }

    public PagedResponse<OrganizationGroupResponse> listGroups(UUID organizationId, ListGroupsRequest request) {
        Sort sort = Sort.by("createdAt").ascending();

        Page<OrganizationGroup> page;
        if (request.getName() != null && !request.getName().isBlank()) {
            page = organizationGroupRepository.findAllByOrganizationIdAndNameContainingIgnoreCase(
                    organizationId, request.getName().trim(), request.toPageRequest(sort));
        } else {
            page = organizationGroupRepository.findAllByOrganizationId(
                    organizationId, request.toPageRequest(sort));
        }

        return PagedResponse.buildFromPage(page.map(OrganizationGroupResponse::buildFromGroup));
    }

    public PagedResponse<OrganizationGroupParticipantResponse> getParticipants(UUID organizationId, UUID groupId, PagedRequest request) {
        currentUserProvider.requireOrganizationMembership(organizationId);

        requireGroup(organizationId, groupId);

        Sort sort = Sort.by("joinedAt").ascending();

        var page = organizationGroupParticipantRepository
                .findAllByOrganizationGroupIdAndOrganizationId(groupId, organizationId, request.toPageRequest(sort))
                .map(OrganizationGroupParticipantResponse::buildFromParticipant);

        return PagedResponse.buildFromPage(page);
    }

    @Transactional
    public OrganizationGroupResponse addParticipant(UUID organizationId, UUID groupId, AddGroupParticipantRequest request) {
        OrganizationMember requester = currentUserProvider.requireOrganizationMembership(organizationId);
        if (!requester.hasPermission(OrganizationPermission.MANAGE_GROUPS))
            throw new DomainBusinessRuleException("Você não tem permissão para gerenciar grupos desta organização.");

        OrganizationGroup group = requireGroup(organizationId, groupId);

        OrganizationMember memberToAdd = organizationMemberRepository
                .findByIdAndOrganizationIdAndStatus(request.memberId(), organizationId, OrganizationMemberStatus.ACTIVE)
                .orElseThrow(() -> new DomainNotFoundException("Membro não encontrado nessa organização."));

        group.addParticipant(memberToAdd);
        organizationGroupRepository.save(group);
        syncEventParticipantsService.executeForMember(organizationId, memberToAdd);

        return OrganizationGroupResponse.buildFromGroup(group);
    }

    @Transactional
    public OrganizationGroupResponse removeParticipant(UUID organizationId, UUID groupId, UUID participantId) {
        OrganizationMember requester = currentUserProvider.requireOrganizationMembership(organizationId);
        if (!requester.hasPermission(OrganizationPermission.MANAGE_GROUPS))
            throw new DomainBusinessRuleException("Você não tem permissão para gerenciar grupos desta organização.");

        OrganizationGroup group = requireGroup(organizationId, groupId);

        OrganizationMember removedMember = group.getParticipants().stream()
                .filter(p -> p.getId().equals(participantId))
                .map(OrganizationGroupParticipant::getOrganizationMember)
                .findFirst()
                .orElseThrow(() -> new DomainNotFoundException("Participante não encontrado neste grupo."));

        group.removeParticipant(participantId);
        organizationGroupRepository.save(group);
        syncEventParticipantsService.executeForMember(organizationId, removedMember);

        return OrganizationGroupResponse.buildFromGroup(group);
    }

    @Transactional
    public OrganizationGroupResponse clearGroup(UUID organizationId, UUID groupId) {
        OrganizationMember requester = currentUserProvider.requireOrganizationMembership(organizationId);
        if (!requester.hasPermission(OrganizationPermission.MANAGE_GROUPS))
            throw new DomainBusinessRuleException("Você não tem permissão para gerenciar grupos desta organização.");

        OrganizationGroup group = requireGroup(organizationId, groupId);

        List<OrganizationMember> removedMembers = group.getParticipants().stream()
                .map(OrganizationGroupParticipant::getOrganizationMember)
                .toList();

        group.clearParticipants();
        organizationGroupRepository.save(group);
        syncEventParticipantsService.executeForMembers(organizationId, removedMembers);

        return OrganizationGroupResponse.buildFromGroup(group);
    }

    @Transactional
    public void deleteGroup(UUID organizationId, UUID groupId) {
        OrganizationMember requester = currentUserProvider.requireOrganizationMembership(organizationId);
        if (!requester.hasPermission(OrganizationPermission.MANAGE_GROUPS))
            throw new DomainBusinessRuleException("Você não tem permissão para gerenciar grupos desta organização.");

        OrganizationGroup group = requireGroup(organizationId, groupId);

        organizationGroupRepository.delete(group);
    }

    private OrganizationGroup requireGroup(UUID organizationId, UUID groupId) {
        return organizationGroupRepository.findByIdAndOrganizationId(groupId, organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Grupo não encontrado nessa organização."));
    }
}

