package br.com.convoquei.backend.domain.organization.event.model.enums;

public enum OrganizationEventParticipantStatusEnum {
    INVITED("Convidado"),
    ACCEPTED("Aceito"),
    DECLINED("Recusado"),
    MAYBE("Talvez"),
    CHECKED_IN("Presente");

    private final String title;

    OrganizationEventParticipantStatusEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
