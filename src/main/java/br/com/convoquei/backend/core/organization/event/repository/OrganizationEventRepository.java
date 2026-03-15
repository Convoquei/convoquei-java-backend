package br.com.convoquei.backend.core.organization.event.repository;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;
import br.com.convoquei.backend.sharedkernel.repository.BaseRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationEventRepository extends BaseRepository<OrganizationEvent, UUID>, OrganizationEventRepositoryCustom {

    @EntityGraph(attributePaths = {"participants", "participants.member", "calledFunctions", "calledFunctions.function", "cancelledBy", "cancelledBy.user"})
    Optional<OrganizationEvent> findByIdAndOrganizationId(UUID id, UUID organizationId);

    @EntityGraph(attributePaths = {"participants", "participants.function", "calledFunctions", "calledFunctions.function", "calledGroups", "calledGroups.group", "cancelledBy", "cancelledBy.user"})
    Optional<OrganizationEvent> findFullDetailByIdAndOrganizationId(UUID id, UUID organizationId);

    @EntityGraph(attributePaths = {"participants", "participants.member", "calledFunctions", "calledFunctions.function", "calledGroups", "calledGroups.group"})
    List<OrganizationEvent> findAllByOrganizationIdAndStatus(UUID organizationId, OrganizationEventStatus status);

    List<OrganizationEvent> findByStatusAndStartAtLessThanEqualAndEndAtGreaterThan(OrganizationEventStatus status, OffsetDateTime now1, OffsetDateTime now2);

    List<OrganizationEvent> findByStatusNotInAndEndAtLessThanEqual(Collection<OrganizationEventStatus> statuses, OffsetDateTime now);

    @Query(value = "SELECT * FROM organization_event e WHERE e.status = 'SCHEDULED' AND e.allow_assignments = true AND e.start_at - (e.availability_lock_before_start_in_minutes * interval '1 minute') <= :now", nativeQuery = true)
    List<OrganizationEvent> findScheduledEventsWithExpiredAvailabilityLock(@Param("now") OffsetDateTime now);
}