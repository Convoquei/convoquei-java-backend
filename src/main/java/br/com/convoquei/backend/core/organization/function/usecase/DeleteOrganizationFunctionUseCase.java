package br.com.convoquei.backend.core.organization.function.usecase;

import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import br.com.convoquei.backend.core.organization.function.repository.OrganizationFunctionRepository;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteOrganizationFunctionUseCase {

    private final OrganizationFunctionRepository organizationFunctionRepository;

    public DeleteOrganizationFunctionUseCase(OrganizationFunctionRepository organizationFunctionRepository) {
        this.organizationFunctionRepository = organizationFunctionRepository;
    }

    @Transactional
    public void execute(UUID organizationId, UUID functionId) {
        OrganizationFunction function = organizationFunctionRepository.requireByIdAndOrganizationId(functionId, organizationId);
        organizationFunctionRepository.delete(function);
    }
}
