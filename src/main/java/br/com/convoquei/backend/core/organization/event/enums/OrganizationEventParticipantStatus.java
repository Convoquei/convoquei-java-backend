package br.com.convoquei.backend.core.organization.event.enums;

public enum OrganizationEventParticipantStatus {
    PENDING("Pendente"),
    AWAITING_ASSIGNMENT("Aguardando aprovação da escala"),
    ASSIGNED("Escalado"),
    NOT_ASSIGNED("Não escalado"),
    EXPIRED("Resposta expirada");

    private final String title;

    OrganizationEventParticipantStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
