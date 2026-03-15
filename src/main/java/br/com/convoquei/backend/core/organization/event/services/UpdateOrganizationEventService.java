package br.com.convoquei.backend.core.organization.event.services;

import br.com.convoquei.backend.core.organization.event.api.dto.request.UpdateOrganizationEventRequest;
import br.com.convoquei.backend.core.organization.event.api.dto.response.OrganizationEventDetailResponse;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventCallMode;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventFunctionCallMode;
import br.com.convoquei.backend.core.organization.event.repository.OrganizationEventRepository;
import br.com.convoquei.backend.core.organization.event.valueobject.OrganizationEventConfiguration;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateOrganizationEventService {

    private final CurrentUserProvider currentUserProvider;
    private final OrganizationEventRepository organizationEventRepository;

    public UpdateOrganizationEventService(CurrentUserProvider currentUserProvider,
                                          OrganizationEventRepository organizationEventRepository) {
        this.currentUserProvider = currentUserProvider;
        this.organizationEventRepository = organizationEventRepository;
    }

    @Transactional
    public OrganizationEventDetailResponse execute(UUID organizationId, UUID eventId, UpdateOrganizationEventRequest request) {
        OrganizationMember member = currentUserProvider.requireOrganizationMembership(organizationId);
        Organization organization = member.getOrganization();

        OrganizationEvent event = organizationEventRepository
                .findFullDetailByIdAndOrganizationId(eventId, organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Evento não encontrado nesta organização."));

        OrganizationEventConfiguration newConfiguration = buildConfiguration(request);

        event.update(newConfiguration, request.startDate(), request.endDate());

        applyCalledGroupsIfNecessary(event, request, organization);
        applyCalledFunctionsIfNecessary(event, request, organization);
        event.syncParticipants();

        organizationEventRepository.save(event);

        return OrganizationEventDetailResponse.from(event);
    }

    private OrganizationEventConfiguration buildConfiguration(UpdateOrganizationEventRequest request) {
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

    private void applyCalledGroupsIfNecessary(OrganizationEvent event, UpdateOrganizationEventRequest request, Organization organization) {
        if (event.getConfiguration().getCallMode() != OrganizationEventCallMode.ONLY_SPECIFIED_GROUP_MEMBERS)
            return;

        if (request.calledGroupIds() == null || request.calledGroupIds().isEmpty())
            throw new DomainBusinessRuleException("Informe ao menos um grupo para a convocação do tipo 'Grupos Especificos'.");

        request.calledGroupIds().forEach(groupId -> {
            var group = organization.getGroups().stream()
                    .filter(g -> g.getId().equals(groupId))
                    .findFirst()
                    .orElseThrow(() -> new DomainBusinessRuleException("Um dos grupos especificados para convocação não foi encontrado na organização."));

            event.addCalledGroups(group);
        });
    }

    private void applyCalledFunctionsIfNecessary(OrganizationEvent event, UpdateOrganizationEventRequest request, Organization organization) {
        if (event.getConfiguration().getFunctionCallMode() == null)
            return;

        if (request.calledFunctionIds() != null && request.calledFunctionIds().isEmpty())
            throw new DomainBusinessRuleException("Informe ao menos uma funcao para que as regras de escalacao possam ser aplicadas.");

        if (request.calledFunctionIds() != null) {
            request.calledFunctionIds().forEach(requestFunction -> {
                var function = organization.getFunctions().stream()
                        .filter(f -> f.getId().equals(requestFunction.id()))
                        .findFirst()
                        .orElseThrow(() -> new DomainBusinessRuleException("Uma das funções especificadas para convocação não foi encontrada na organização."));

                event.addCalledFunctions(function, requestFunction.minMembers(), requestFunction.maxMembers(), requestFunction.assignmentMode());
            });
        }
    }

}
