package br.com.convoquei.backend.organizationEvent.model.entity;

import br.com.convoquei.backend._shared.model.entity.BaseEntity;
import br.com.convoquei.backend.organizationEvent.model.enums.EventParticipantAvailabilityStatus;
import br.com.convoquei.backend.organizationGroup.model.entity.OrganizationGroup;
import br.com.convoquei.backend.organizationMember.model.entity.OrganizationMember;
import jakarta.persistence.*;

@Entity
@Table(name = "organization_event_participant")
public class OrganizationEventParticipant extends BaseEntity {

    protected OrganizationEventParticipant() {
    }

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

    @Column(name = "display_name_snapshot", length = 70)
    private String displayNameSnapshot;

    @Column(name = "avatar_url_snapshot")
    private String avatarUrlSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", length = 20, nullable = false)
    private EventParticipantAvailabilityStatus availabilityStatus = EventParticipantAvailabilityStatus.NOT_ANSWERED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "source_group_id",
            foreignKey = @ForeignKey(name = "fk_event_participant_source_group"),
            nullable = true
    )
    private OrganizationGroup sourceGroup;
}
