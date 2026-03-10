package br.com.convoquei.backend.core.organization.event.entity;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantStatus;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import br.com.convoquei.backend.sharedkernel.model.entity.BaseEntity;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventParticipantAvailabilityStatus;
import br.com.convoquei.backend.core.organization.group.entity.OrganizationGroup;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import jakarta.persistence.*;

@Entity
@Table(name = "organization_event_participant")
public class OrganizationEventParticipant extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_event_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_event_participant_event")
    )
    private OrganizationEvent event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "organization_member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_event_participant_member")
    )
    private OrganizationMember member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_function_id",
            foreignKey = @ForeignKey(name = "fk_organization_event_participant_function")
    )
    private OrganizationFunction function;

    @Column(name = "display_name_snapshot", length = 70)
    private String displayNameSnapshot;

    @Column(name = "avatar_url_snapshot")
    private String avatarUrlSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private OrganizationEventParticipantStatus status = OrganizationEventParticipantStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", length = 30, nullable = false)
    private OrganizationEventParticipantAvailabilityStatus availabilityStatus = OrganizationEventParticipantAvailabilityStatus.NOT_ANSWERED;

    protected OrganizationEventParticipant() {
    }

    public OrganizationEventParticipant(OrganizationEvent event, OrganizationMember member, String displayNameSnapshot, String avatarUrlSnapshot) {
        this.event = event;
        this.member = member;
        this.displayNameSnapshot = displayNameSnapshot;
        this.avatarUrlSnapshot = avatarUrlSnapshot;
    }

    public OrganizationEvent getEvent() {
        return event;
    }

    public OrganizationMember getMember() {
        return member;
    }

    public String getDisplayNameSnapshot() {
        return displayNameSnapshot;
    }

    public String getAvatarUrlSnapshot() {
        return avatarUrlSnapshot;
    }

    public OrganizationEventParticipantAvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public OrganizationEventParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(OrganizationEventParticipantStatus status) {
        this.status = status;
    }
}
