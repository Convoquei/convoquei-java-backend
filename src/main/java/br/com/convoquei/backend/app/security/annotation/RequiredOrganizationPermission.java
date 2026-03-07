package br.com.convoquei.backend.app.security.annotation;

import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredOrganizationPermission {
    OrganizationPermission value();
}