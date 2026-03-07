package br.com.convoquei.backend.core.organization.organization.repository.specifications;

import br.com.convoquei.backend.sharedkernel.helpers.SpecHelper;
import br.com.convoquei.backend.core.organization.organization.api.dto.request.ListOrganizationsByMemberRequest;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class OrganizationSpec {
    private OrganizationSpec() { }

    public static Specification<Organization> memberUserId(UUID userId) {
        return (root, query, criteriaBuilder) -> {
            //query.distinct(true);
            var members = root.join("members");
            var user = members.join("user");

            query.orderBy(criteriaBuilder.asc(members.get("joinedAt")));

            return criteriaBuilder.equal(user.get("id"), userId);
        };
    }

    public static Specification<Organization> from(ListOrganizationsByMemberRequest request) {
        Specification<Organization> spec = generateBaseSpec();

        if (request.getName() != null && !request.getName().isBlank()) {
            spec = spec.and(SpecHelper.startsWithIgnoreCase("name", request.getName().trim()));
        }

        return spec;
    }

    private static Specification<Organization> generateBaseSpec() {
        return (root, query, cb) -> cb.conjunction();
    }
}
