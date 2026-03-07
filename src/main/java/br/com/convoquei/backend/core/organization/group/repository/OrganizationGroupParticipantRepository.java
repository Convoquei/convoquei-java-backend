package br.com.convoquei.backend.core.organization.group.repository;
import br.com.convoquei.backend.sharedkernel.repository.BaseRepository;
import br.com.convoquei.backend.core.organization.group.entity.OrganizationGroupParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
public interface OrganizationGroupParticipantRepository extends BaseRepository<OrganizationGroupParticipant, UUID> {
    void deleteAllByOrganizationMemberId(UUID organizationMemberId);

    void deleteAllByOrganizationMemberIdAndOrganizationId(UUID organizationMemberId, UUID organizationId);

    @EntityGraph(attributePaths = {"organizationMember", "organizationMember.user"})
    Page<OrganizationGroupParticipant> findAllByOrganizationGroupIdAndOrganizationId(UUID organizationGroupId, UUID organizationId, Pageable pageable);

    @EntityGraph(attributePaths = {"organizationMember", "organizationGroup"})
    List<OrganizationGroupParticipant> findAllByOrganizationMemberIdIn(Collection<UUID> memberIds);
}
