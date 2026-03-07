package br.com.convoquei.backend.core.organization.role.services;

import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import br.com.convoquei.backend.app.api.dto.request.PagedRequest;
import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.organization.organization.repository.OrganizationRepository;
import br.com.convoquei.backend.core.organization.organization.repository.OrganizationRoleRepository;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.member.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.core.organization.member.repository.OrganizationMemberRepository;
import br.com.convoquei.backend.core.organization.role.api.dto.request.CreateOrganizationRoleRequest;
import br.com.convoquei.backend.core.organization.role.api.dto.request.UpdateOrganizationRolePermissionsRequest;
import br.com.convoquei.backend.core.organization.role.api.dto.response.OrganizationPermissionResponse;
import br.com.convoquei.backend.core.organization.role.api.dto.response.OrganizationRoleResponse;
import br.com.convoquei.backend.core.organization.role.api.dto.response.OrganizationRoleMemberResponse;
import br.com.convoquei.backend.core.organization.role.entity.OrganizationRole;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationDefaultSystemRole;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationRoleService {

    private final OrganizationRoleRepository organizationRoleRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public OrganizationRoleService(OrganizationRoleRepository organizationRoleRepository,
                                   OrganizationRepository organizationRepository,
                                   OrganizationMemberRepository organizationMemberRepository) {
        this.organizationRoleRepository = organizationRoleRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
    }

    public List<OrganizationPermissionResponse> listAvailablePermissions() {
        Set<String> reservedKeys = Arrays.stream(OrganizationDefaultSystemRole.values())
                .map(OrganizationDefaultSystemRole::getSystemKey)
                .collect(Collectors.toSet());

        return Arrays.stream(OrganizationPermission.values())
                .filter(p -> !reservedKeys.contains(p.name()))
                .map(OrganizationPermissionResponse::from)
                .toList();
    }

    public PagedResponse<OrganizationRoleResponse> listRoles(UUID organizationId, PagedRequest request) {
        var page = organizationRoleRepository
                .findAllByOrganizationIdAndSystemKeyIsNull(organizationId, request.toPageRequest(Sort.by("name").ascending()))
                .map(OrganizationRoleResponse::from);

        return PagedResponse.buildFromPage(page);
    }

    public OrganizationRoleResponse getRole(UUID organizationId, UUID roleId) {
        return OrganizationRoleResponse.from(findRole(organizationId, roleId));
    }

    @Transactional
    public OrganizationRoleResponse createRole(UUID organizationId, CreateOrganizationRoleRequest request) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Organização não encontrada."));

        EnumSet<OrganizationPermission> permissions = parseAndValidatePermissions(request.permissions());

        OrganizationRole role = OrganizationRole.createCustomRole(organization, request.name(), permissions);
        organizationRoleRepository.save(role);

        return OrganizationRoleResponse.from(role);
    }

    @Transactional
    public OrganizationRoleResponse updateRolePermissions(UUID organizationId, UUID roleId,
                                                          UpdateOrganizationRolePermissionsRequest request) {
        OrganizationRole role = findRole(organizationId, roleId);

        if (role.isSystem()) {
            throw new DomainBusinessRuleException("Não é permitido editar as permissões de um papel de sistema.");
        }

        EnumSet<OrganizationPermission> permissions = parseAndValidatePermissions(request.permissions());
        role.updatePermissions(permissions);
        organizationRoleRepository.save(role);

        return OrganizationRoleResponse.from(role);
    }

    @Transactional
    public void deleteRole(UUID organizationId, UUID roleId) {
        OrganizationRole role = findRole(organizationId, roleId);

        if (role.isSystem()) {
            throw new DomainBusinessRuleException("Não é permitido deletar um papel de sistema.");
        }

        organizationRoleRepository.delete(role);
    }

    @Transactional
    public void assignRole(UUID organizationId, UUID roleId, UUID memberId) {
        OrganizationRole role = findRole(organizationId, roleId);

        if (role.isSystem()) {
            throw new DomainBusinessRuleException("Não é permitido atribuir um papel de sistema manualmente.");
        }

        OrganizationMember member = findActiveMember(organizationId, memberId);

        boolean alreadyAssigned = member.getRoles().stream()
                .anyMatch(mr -> mr.getOrganizationRole().getId().equals(roleId));

        if (alreadyAssigned) {
            throw new DomainBusinessRuleException("O membro já possui esse papel.");
        }

        member.addRole(role);
        organizationMemberRepository.save(member);
    }

    @Transactional
    public void unassignRole(UUID organizationId, UUID roleId, UUID memberId) {
        OrganizationRole role = findRole(organizationId, roleId);

        if (role.isSystem()) {
            throw new DomainBusinessRuleException("Não é permitido remover um papel de sistema manualmente.");
        }

        OrganizationMember member = findActiveMember(organizationId, memberId);

        boolean removed = member.getRoles().removeIf(mr -> mr.getOrganizationRole().getId().equals(roleId));

        if (!removed) {
            throw new DomainBusinessRuleException("O membro não possui esse papel.");
        }

        organizationMemberRepository.save(member);
    }

    public PagedResponse<OrganizationRoleMemberResponse> listRoleMembers(UUID organizationId, UUID roleId, PagedRequest request) {
        OrganizationRole role = findRole(organizationId, roleId);

        var page = organizationMemberRepository
                .findActiveByOrganizationIdAndRoleId(organizationId, OrganizationMemberStatus.ACTIVE, role.getId(), request.toPageRequest())
                .map(OrganizationRoleMemberResponse::buildForMember);

        return PagedResponse.buildFromPage(page);
    }

    private OrganizationRole findRole(UUID organizationId, UUID roleId) {
        return organizationRoleRepository.findByIdAndOrganizationId(roleId, organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Papel não encontrado nessa organização."));
    }

    private OrganizationMember findActiveMember(UUID organizationId, UUID memberId) {
        return organizationMemberRepository
                .findByIdAndOrganizationIdAndStatus(memberId, organizationId, OrganizationMemberStatus.ACTIVE)
                .orElseThrow(() -> new DomainNotFoundException("Membro ativo não encontrado nessa organização."));
    }

    private EnumSet<OrganizationPermission> parseAndValidatePermissions(List<String> rawPermissions) {
        Set<String> reservedKeys = Arrays.stream(OrganizationDefaultSystemRole.values())
                .map(OrganizationDefaultSystemRole::getSystemKey)
                .collect(Collectors.toSet());

        EnumSet<OrganizationPermission> permissions = EnumSet.noneOf(OrganizationPermission.class);

        for (String raw : rawPermissions) {
            OrganizationPermission permission;
            try {
                permission = OrganizationPermission.valueOf(raw.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new DomainBusinessRuleException("Permissão inválida: " + raw);
            }

            if (reservedKeys.contains(permission.name())) {
                throw new DomainBusinessRuleException(
                        "A permissão '" + permission.name() + "' é reservada ao sistema e não pode ser atribuída a papéis customizados.");
            }

            permissions.add(permission);
        }

        return permissions;
    }
}





