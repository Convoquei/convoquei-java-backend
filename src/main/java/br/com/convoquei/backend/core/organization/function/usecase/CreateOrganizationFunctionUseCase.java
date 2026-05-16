package br.com.convoquei.backend.core.organization.function.usecase;

import br.com.convoquei.backend.core.organization.function.api.dto.request.CreateFunctionRequest;
import br.com.convoquei.backend.core.organization.function.api.dto.response.OrganizationFunctionResponse;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import br.com.convoquei.backend.core.organization.function.repository.OrganizationFunctionRepository;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.organization.organization.repository.OrganizationRepository;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateOrganizationFunctionUseCase {

    private final OrganizationFunctionRepository organizationFunctionRepository;
    private final OrganizationRepository organizationRepository;

    public CreateOrganizationFunctionUseCase(
            OrganizationFunctionRepository organizationFunctionRepository,
            OrganizationRepository organizationRepository
    ) {
        this.organizationFunctionRepository = organizationFunctionRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public OrganizationFunctionResponse execute(UUID organizationId, CreateFunctionRequest request) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new DomainNotFoundException("Organização não encontrada."));

        OrganizationFunction function = new OrganizationFunction(organization, request.name(), request.emoji());
        organizationFunctionRepository.save(function);

        return OrganizationFunctionResponse.buildFromFunction(function);
    }
}
