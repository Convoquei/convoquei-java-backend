package br.com.convoquei.backend.core.organization.function.repository;

import br.com.convoquei.backend.sharedkernel.repository.BaseRepository;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunctionMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OrganizationFunctionMemberRepository extends BaseRepository<OrganizationFunctionMember, UUID> {

    void deleteAllByOrganizationMemberId(UUID organizationMemberId);

    void deleteAllByOrganizationMemberIdAndOrganizationId(UUID organizationMemberId, UUID organizationId);

    @EntityGraph(attributePaths = {"organizationMember", "organizationMember.user"})
    Page<OrganizationFunctionMember> findAllByOrganizationFunctionIdAndOrganizationId(UUID organizationFunctionId, UUID organizationId, Pageable pageable);

    @EntityGraph(attributePaths = {"organizationMember", "organizationFunction"})
    List<OrganizationFunctionMember> findAllByOrganizationMemberIdIn(Collection<UUID> memberIds);
}
