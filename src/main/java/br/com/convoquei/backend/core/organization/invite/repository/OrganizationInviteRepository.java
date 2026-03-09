package br.com.convoquei.backend.core.organization.invite.repository;

import br.com.convoquei.backend.sharedkernel.repository.BaseRepository;
import br.com.convoquei.backend.core.organization.invite.api.dto.response.OrganizationInviteDetailsResponse;
import br.com.convoquei.backend.core.organization.invite.entity.OrganizationInvite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationInviteRepository extends BaseRepository<OrganizationInvite, UUID> {
    boolean existsByOrganizationIdAndInvitedEmail(UUID organizationId, String invitedEmail);
    Optional<OrganizationInvite> findByOrganizationIdAndInvitedEmail(UUID organizationId, String invitedEmail);

    @EntityGraph(attributePaths = {"organization"})
    Optional<OrganizationInvite> findWithOrganizationById(UUID id);

    Page<OrganizationInviteDetailsResponse> findAllByInvitedEmailIgnoreCaseAndExpiresAtAfter(String invitedEmail, Instant expiresAtAfter, Pageable pageable);
}


