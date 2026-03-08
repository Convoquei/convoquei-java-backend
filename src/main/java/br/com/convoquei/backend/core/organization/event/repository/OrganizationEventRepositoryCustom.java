package br.com.convoquei.backend.core.organization.event.repository;

import br.com.convoquei.backend.core.organization.event.repository.queries.FindMemberEventsQuery;
import br.com.convoquei.backend.core.organization.event.repository.views.OrganizationEventMemberView;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface OrganizationEventRepositoryCustom {
    Page<OrganizationEventMemberView> findEventsForMember(UUID organizationId, UUID organizationMemberId, FindMemberEventsQuery query);
}
