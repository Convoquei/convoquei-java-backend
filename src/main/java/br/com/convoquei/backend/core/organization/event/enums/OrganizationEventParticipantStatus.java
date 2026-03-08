package br.com.convoquei.backend.core.organization.event.enums;

public enum OrganizationEventParticipantStatus {
    PENDING("Pendente"),
    AWAITING_ASSIGNMENT("Aguardando escalação"),
    AWAITING_DRAW("Aguardando sorteio de escalação"),
    ASSIGNED("Escalado"),
    NOT_ASSIGNED("Não escalado");

    private final String title;

    OrganizationEventParticipantStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
