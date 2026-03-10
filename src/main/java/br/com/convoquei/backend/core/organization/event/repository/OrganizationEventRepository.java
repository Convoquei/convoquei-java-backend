package br.com.convoquei.backend.core.organization.event.repository;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;
import br.com.convoquei.backend.sharedkernel.repository.BaseRepository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OrganizationEventRepository extends BaseRepository<OrganizationEvent, UUID>, OrganizationEventRepositoryCustom {
    List<OrganizationEvent> findByStatusAndStartAtLessThanEqualAndEndAtGreaterThan(OrganizationEventStatus status, OffsetDateTime now1, OffsetDateTime now2);

    List<OrganizationEvent> findByStatusNotInAndEndAtLessThanEqual(Collection<OrganizationEventStatus> statuses, OffsetDateTime now);
}