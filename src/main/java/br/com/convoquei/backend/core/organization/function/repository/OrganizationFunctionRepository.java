package br.com.convoquei.backend.core.organization.function.repository;

import br.com.convoquei.backend.sharedkernel.repository.BaseRepository;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationFunctionRepository extends BaseRepository<OrganizationFunction, UUID> {

    @EntityGraph(attributePaths = {"members", "members.organizationMember", "members.organizationMember.user"})
    Optional<OrganizationFunction> findByIdAndOrganizationId(UUID id, UUID organizationId);

    Page<OrganizationFunction> findAllByOrganizationId(UUID organizationId, Pageable pageable);

    Page<OrganizationFunction> findAllByOrganizationIdAndNameContainingIgnoreCase(UUID organizationId, String name, Pageable pageable);

    List<OrganizationFunction> findAllByIdInAndOrganizationId(Collection<UUID> ids, UUID organizationId);
}
