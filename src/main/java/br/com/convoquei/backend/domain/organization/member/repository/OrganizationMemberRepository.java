package br.com.convoquei.backend.domain.organization.member.repository;

import br.com.convoquei.backend.sharedkernel.repository.BaseRepository;
import br.com.convoquei.backend.domain.organization.member.model.entity.OrganizationMember;
import br.com.convoquei.backend.domain.organization.member.model.enums.OrganizationMemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationMemberRepository extends BaseRepository<OrganizationMember, UUID> {

    @EntityGraph(attributePaths = {"user", "roles", "roles.organizationRole"})
    Optional<OrganizationMember> findByOrganizationIdAndUserIdAndStatus(UUID organizationId, UUID userId, OrganizationMemberStatus status);

    Optional<OrganizationMember> findByIdAndOrganizationIdAndStatus(UUID id, UUID organizationId, OrganizationMemberStatus status);

    boolean existsByOrganizationIdAndUserIdAndStatus(UUID organizationId, UUID userId, OrganizationMemberStatus status);

    boolean existsByOrganizationIdAndUserEmailIgnoreCase(UUID organizationId, String email);

    @Query("""
            SELECT om FROM OrganizationMember om
            JOIN FETCH om.user
            JOIN om.roles omr
            WHERE om.organization.id = :organizationId
              AND om.status = :status
              AND omr.organizationRole.id = :roleId
            ORDER BY om.user.name ASC
            """)
    Page<OrganizationMember> findActiveByOrganizationIdAndRoleId(
            @Param("organizationId") UUID organizationId,
            @Param("status") OrganizationMemberStatus status,
            @Param("roleId") UUID roleId,
            Pageable pageable);

}
