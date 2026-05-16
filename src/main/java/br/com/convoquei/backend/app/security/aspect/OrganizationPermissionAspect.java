package br.com.convoquei.backend.app.security.aspect;

import br.com.convoquei.backend.app.security.annotation.RequiredOrganizationPermission;
import br.com.convoquei.backend.app.security.context.OrganizationContext;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.role.services.OrganizationPermissionService;
import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class OrganizationPermissionAspect {

    private final OrganizationPermissionService organizationPermissionService;
    private final OrganizationContext organizationContext;
    private final HttpServletRequest request;
    private final CurrentUserProvider currentUserProvider;

    public OrganizationPermissionAspect(OrganizationPermissionService organizationPermissionService,
                                        OrganizationContext organizationContext,
                                        HttpServletRequest request,
                                        CurrentUserProvider currentUserProvider) {
        this.organizationPermissionService = organizationPermissionService;
        this.organizationContext = organizationContext;
        this.request = request;
        this.currentUserProvider = currentUserProvider;
    }

    @Before("@annotation(requiredPermission)")
    public void checkPermission(JoinPoint joinPoint, RequiredOrganizationPermission requiredPermission) {
        UUID organizationId = resolveOrganizationId();
        UUID userId = currentUserProvider.requireUserId();

        OrganizationMember member = organizationPermissionService.requireAuthorizedMember(
                organizationId, userId, requiredPermission.value());

        organizationContext.setMember(member);
    }

    private UUID resolveOrganizationId() {
        String[] segments = request.getRequestURI().split("/");
        for (int i = 0; i < segments.length - 1; i++) {
            if ("organizations".equals(segments[i])) {
                return UUID.fromString(segments[i + 1]);
            }
        }
        throw new IllegalStateException("organizationId não encontrado na URL.");
    }
}
