package br.com.convoquei.backend.core.organization.function.usecase;

import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.core.organization.function.api.dto.request.ListFunctionsRequest;
import br.com.convoquei.backend.core.organization.function.api.dto.response.OrganizationFunctionResponse;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import br.com.convoquei.backend.core.organization.function.repository.OrganizationFunctionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ListOrganizationFunctionsUseCase {

    private final OrganizationFunctionRepository organizationFunctionRepository;

    public ListOrganizationFunctionsUseCase(OrganizationFunctionRepository organizationFunctionRepository) {
        this.organizationFunctionRepository = organizationFunctionRepository;
    }

    public PagedResponse<OrganizationFunctionResponse> execute(UUID organizationId, ListFunctionsRequest request) {
        Sort sort = Sort.by("createdAt").ascending();

        Page<OrganizationFunction> page;
        if (request.getName() != null && !request.getName().isBlank()) {
            page = organizationFunctionRepository.findAllByOrganizationIdAndNameContainingIgnoreCase(
                    organizationId, request.getName().trim(), request.toPageRequest(sort));
        } else {
            page = organizationFunctionRepository.findAllByOrganizationId(
                    organizationId, request.toPageRequest(sort));
        }

        return PagedResponse.buildFromPage(page.map(OrganizationFunctionResponse::buildFromFunction));
    }
}
