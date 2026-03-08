package br.com.convoquei.backend.core.organization.event.services;

import br.com.convoquei.backend.core.organization.event.api.dto.request.CreateCreateOrganizationEventRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.response.OrganizationEventMembershipResponse;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventCallMode;
import br.com.convoquei.backend.core.organization.event.repository.OrganizationEventRepository;
import br.com.convoquei.backend.core.organization.event.valueobject.OrganizationEventConfiguration;
import br.com.convoquei.backend.core.organization.group.repository.OrganizationGroupRepository;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.member.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateOrganizationEventService {
    private final CurrentUserProvider currentUserProvider;
    private final OrganizationEventRepository organizationEventRepository;

    public CreateOrganizationEventService(CurrentUserProvider currentUserProvider, OrganizationEventRepository organizationEventRepository, OrganizationGroupRepository organizationGroupRepository) {
        this.currentUserProvider = currentUserProvider;
        this.organizationEventRepository = organizationEventRepository;
    }

    @Transactional
    public OrganizationEventMembershipResponse execute(UUID organizationId, CreateCreateOrganizationEventRequest request) {
        OrganizationMember member = currentUserProvider.requireOrganizationMembership(organizationId);

        Organization organization = member.getOrganization();

        OrganizationEvent event = new OrganizationEvent(
                organization,
                member,
                buildEventConfigurationFromRequest(request),
                request.startDate(),
                request.endDate()
        );

        applyCalledGroupsIfNecessary(event, request);
        applyCalledFunctionsIfNecessary(event, request);
        addEligibleMembersAsPendingParticipants(organization, event);

        organizationEventRepository.save(event);

        return OrganizationEventMembershipResponse.buildFromEventAndMembership(event, member.getId());
    }

    private static OrganizationEventConfiguration buildEventConfigurationFromRequest(CreateCreateOrganizationEventRequest request) {
        return new OrganizationEventConfiguration(
                request.name(),
                request.description(),
                request.location(),
                request.assignmentMode(),
                request.callMode(),
                request.functionCallMode(),
                request.availabilityLockBeforeStartInMinutes()
        );
    }

    private void applyCalledGroupsIfNecessary(OrganizationEvent event, CreateCreateOrganizationEventRequest request) {
        if (event.getConfiguration().getCallMode() != OrganizationEventCallMode.ONLY_SPECIFIED_GROUP_MEMBERS)
            return;

        if (request.calledGroupIds() == null || request.calledGroupIds().isEmpty())
            throw new DomainBusinessRuleException("Informe ao menos um grupo para a convocação do tipo 'Grupos Especificos'.");

        Organization organization = event.getOrganization();

        request.calledGroupIds().forEach(groupId -> {
            var group = organization.getGroups().stream()
                    .filter(g -> g.getId().equals(groupId))
                    .findFirst()
                    .orElseThrow(() -> new DomainBusinessRuleException("Um dos grupos especificados para convocação não foi encontrado na organização."));

            event.addCalledGroups(group);
        });
    }

    private void applyCalledFunctionsIfNecessary(OrganizationEvent event, CreateCreateOrganizationEventRequest request) {
        if (event.getConfiguration().getFunctionCallMode() == null)
            return;

        if (request.calledFunctionIds() == null || request.calledFunctionIds().isEmpty())
            throw new DomainBusinessRuleException("Informe ao menos uma função caso queira utilizar o recurso de convocação por função.");

        Organization organization = event.getOrganization();

        request.calledFunctionIds().forEach(functionId -> {
            var function = organization.getFunctions().stream()
                    .filter(f -> f.getId().equals(functionId))
                    .findFirst()
                    .orElseThrow(() -> new DomainBusinessRuleException("Uma das funções especificadas para convocação não foi encontrada na organização."));

            event.addCalledFunctions(function);
        });
    }

    private void addEligibleMembersAsPendingParticipants(Organization organization, OrganizationEvent event) {
        organization.getMembers().stream()
                .filter(m -> m.getStatus() == OrganizationMemberStatus.ACTIVE)
                .filter(m -> event.canMemberParticipate(m).success())
                .forEach(event::addParticipant);
    }
}
