package br.com.convoquei.backend.core.organization.event.enums;

import br.com.convoquei.backend.sharedkernel.model.enums.EnumBase;

public enum OrganizationEventCallMode implements EnumBase {
    ALL_ORGANIZATION_MEMBERS("Permitir participação de todos os membros da organização"),
    ONLY_SPECIFIED_GROUP_MEMBERS("Permitir participação apenas de membros dos grupos selecionados");

    private final String title;

    OrganizationEventCallMode(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
