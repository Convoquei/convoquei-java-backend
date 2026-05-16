package br.com.convoquei.backend.app.security.context;

import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class OrganizationContext {

    private OrganizationMember member;

    public void setMember(OrganizationMember member) {
        this.member = member;
    }

    public OrganizationMember member() {
        if (member == null) {
            throw new IllegalStateException(
                "OrganizationContext não populado — endpoint sem @RequiredOrganizationPermission?");
        }

        return member;
    }
}
