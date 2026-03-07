package br.com.convoquei.backend.core.organization.organization.services;

import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.core.organization.organization.api.dto.request.ListOrganizationsByMemberRequest;
import br.com.convoquei.backend.core.organization.organization.api.dto.response.OrganizationResponse;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.organization.organization.repository.OrganizationRepository;
import br.com.convoquei.backend.core.organization.organization.repository.specifications.OrganizationSpec;
import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ListOrganizationByMemberService {

    private final CurrentUserProvider currentUserProvider;
    private final OrganizationRepository organizationRepository;

    public ListOrganizationByMemberService(CurrentUserProvider currentUserProvider, OrganizationRepository organizationRepository) {
        this.currentUserProvider = currentUserProvider;
        this.organizationRepository = organizationRepository;
    }

    public PagedResponse<OrganizationResponse> listByMember(ListOrganizationsByMemberRequest request) {
        Specification<Organization> specification = genetareSpecification(request);

        var page = organizationRepository.findAll(specification, request.toPageRequest())
                .map(OrganizationResponse::buildFromOrganization);

        return PagedResponse.buildFromPage(page);
    }

    private Specification<Organization> genetareSpecification(ListOrganizationsByMemberRequest request) {
        UUID userId = currentUserProvider.requireUserId();

        Specification<Organization> specification = OrganizationSpec.memberUserId(userId);

        specification = specification.and(OrganizationSpec.from(request));

        return specification;
    }
}
