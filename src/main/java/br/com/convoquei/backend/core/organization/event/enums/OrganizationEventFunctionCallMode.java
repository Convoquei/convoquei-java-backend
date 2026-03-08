package br.com.convoquei.backend.core.organization.event.enums;

public enum OrganizationEventFunctionCallMode {
    ONLY_MEMBERS_WITH_LISTED_FUNCTIONS("Apenas membros com as funções abaixo participam"),
    ALL_MEMBERS_WITH_FUNCTION_LIMITS("Todos participam, mas as funções possuem limites");

    private final String title;

    OrganizationEventFunctionCallMode(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
