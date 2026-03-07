package br.com.convoquei.backend.core.organization.event.services;

import br.com.convoquei.backend.core.organization.event.api.dto.request.CreateOrganizationEventRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.response.OrganizationEventResponse;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.repository.OrganizationEventRepository;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateOrganizationEventService {
    private final CurrentUserProvider currentUserProvider;
    private final OrganizationEventRepository organizationEventRepository;

    public CreateOrganizationEventService(CurrentUserProvider currentUserProvider, OrganizationEventRepository organizationEventRepository) {
        this.currentUserProvider = currentUserProvider;
        this.organizationEventRepository = organizationEventRepository;
    }

    @Transactional
    public OrganizationEventResponse execute(UUID organizationId, CreateOrganizationEventRequest request) {
        OrganizationMember member = currentUserProvider.requireOrganizationMembership(organizationId);

        Organization organization = member.getOrganization();

        OrganizationEvent event = new OrganizationEvent(
                organization,
                member,
                request.name(),
                request.description(),
                request.location(),
                request.startDate(),
                request.endDate(),
                request.availabilityLockBeforeStartInMinutes(),
                request.assignmentMode()
        );

        organizationEventRepository.save(event);

        return OrganizationEventResponse.buildFromEvent(event);
    }
}
