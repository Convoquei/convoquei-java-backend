package br.com.convoquei.backend.core.organization.event.api.dto.request.interfaces;

import br.com.convoquei.backend.core.organization.event.api.dto.request.CreateCreateOrganizationEventCalledFunctionRequest;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentMode;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventCallMode;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventFunctionCallMode;

import java.util.List;
import java.util.UUID;

public interface CreateOrganizationEventContract {

    String name();

    String description();

    String location();

    OrganizationEventAssignmentMode assignmentMode();

    OrganizationEventCallMode callMode();

    OrganizationEventFunctionCallMode functionCallMode();

    int availabilityLockBeforeStartInMinutes();

    List<CreateCreateOrganizationEventCalledFunctionRequest> calledFunctionIds();

    List<UUID> calledGroupIds();
}