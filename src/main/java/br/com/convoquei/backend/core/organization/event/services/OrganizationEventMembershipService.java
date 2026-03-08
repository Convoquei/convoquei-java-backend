package br.com.convoquei.backend.core.organization.event.services;

import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.core.organization.event.api.dto.request.ListOrganizationEventRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.response.OrganizationEventMembershipResponse;
import br.com.convoquei.backend.core.organization.event.repository.OrganizationEventRepository;
import br.com.convoquei.backend.core.organization.event.repository.queries.FindMemberEventsQuery;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationEventMembershipService {
    private final OrganizationEventRepository organizationEventRepository;
    private final CurrentUserProvider currentUserProvider;

    public OrganizationEventMembershipService(OrganizationEventRepository organizationEventRepository, CurrentUserProvider currentUserProvider) {
        this.organizationEventRepository = organizationEventRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public PagedResponse<OrganizationEventMembershipResponse> listEvents(UUID organizationId, ListOrganizationEventRequest request) {
        OrganizationMember member = currentUserProvider.requireOrganizationMembership(organizationId);

        FindMemberEventsQuery query = buildFindMemberEventsQueryFromRequest(request);

        var result = organizationEventRepository.findEventsForMember(organizationId, member.getId(), query);

        return PagedResponse.buildFromPage(result.map(OrganizationEventMembershipResponse::buildFromEventMembershipView));
    }

    private static FindMemberEventsQuery buildFindMemberEventsQueryFromRequest(ListOrganizationEventRequest request) {
        return new FindMemberEventsQuery(
                request.getName(),
                request.getStartAfter(),
                request.getStartBefore(),
                request.getParticipantStatus(),
                request.toPageRequest()
        );
    }
}
