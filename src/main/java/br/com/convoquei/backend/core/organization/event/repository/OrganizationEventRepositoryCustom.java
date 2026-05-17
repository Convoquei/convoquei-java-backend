package br.com.convoquei.backend.core.organization.event.repository;

import br.com.convoquei.backend.core.organization.event.repository.queries.FindMemberEventsQuery;
import br.com.convoquei.backend.core.organization.event.repository.views.MemberAvailabilityEventView;
import br.com.convoquei.backend.core.organization.event.repository.views.OrganizationEventMemberView;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface OrganizationEventRepositoryCustom {
    Page<OrganizationEventMemberView> findEventsForMember(UUID organizationId, UUID organizationMemberId, FindMemberEventsQuery query);

    List<MemberAvailabilityEventView> findOverlappingEventsForMember(UUID organizationId, UUID memberId, OffsetDateTime start, OffsetDateTime end);
}
