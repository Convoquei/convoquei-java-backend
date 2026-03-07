package br.com.convoquei.backend.core.organization.invite.services;

import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainConflictException;
import br.com.convoquei.backend.core.organization.invite.api.dto.response.OrganizationInviteDetailsResponse;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.organization.invite.entity.OrganizationInvite;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.member.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.core.organization.invite.repository.OrganizationInviteRepository;
import br.com.convoquei.backend.core.organization.member.repository.OrganizationMemberRepository;
import br.com.convoquei.backend.core.organization.organization.repository.OrganizationRepository;
import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationInviteService {

    private final OrganizationInviteRepository organizationInviteRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRepository organizationRepository;
    private final CurrentUserProvider currentUserProvider;

    public OrganizationInviteService(OrganizationInviteRepository organizationInviteRepository, OrganizationMemberRepository organizationMemberRepository, OrganizationRepository organizationRepository, CurrentUserProvider currentUserProvider) {
        this.organizationInviteRepository = organizationInviteRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationRepository = organizationRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public OrganizationInviteDetailsResponse invite(UUID organizationId, String email) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new DomainBusinessRuleException("Não foi possível encontrar a organização especificada."));

        if (isUserInvitedToOrganization(organizationId, email))
            throw new DomainConflictException("O usuário já foi convidado para esta organização.");

        if (isUserMemberBannedFromOrganization(organizationId, organizationId))
            throw new DomainBusinessRuleException("O usuário está banido desta organização e não pode ser convidado.");

        if(isUserMemberOfOrganization(organizationId, email))
            throw new DomainBusinessRuleException("O usuário já é membro desta organização.");

        OrganizationMember organizationMember = currentUserProvider.organizationMembership(organizationId).orElseThrow(() -> new DomainBusinessRuleException("Você precisa ser um membro ativo desta organização para convidar outros usuários."));

        OrganizationInvite invite = organization.createInvite(organizationMember, email);

        organizationInviteRepository.save(invite);

        return OrganizationInviteDetailsResponse.buildFromInvite(invite);
    }

    @Transactional
    public void revoke(UUID organizationId, String email) {
        OrganizationInvite invite = organizationInviteRepository.findByOrganizationIdAndInvitedEmail(organizationId, email)
                .orElseThrow(() -> new DomainBusinessRuleException("Convite não encontrado para o email especificado nesta organização."));

        organizationInviteRepository.delete(invite);
    }

    public boolean isUserInvitedToOrganization(UUID organizationId, String email) {
        return organizationInviteRepository.existsByOrganizationIdAndInvitedEmail(organizationId, email);
    }

    public boolean isUserMemberOfOrganization(UUID organizationId, String email) {
        return organizationMemberRepository.existsByOrganizationIdAndUserEmailIgnoreCase(organizationId, email);
    }

    public boolean isUserMemberBannedFromOrganization(UUID organizationId, UUID userId) {
        return organizationMemberRepository.existsByOrganizationIdAndUserIdAndStatus(organizationId, userId, OrganizationMemberStatus.BANNED);
    }
}
