package br.com.convoquei.backend.organizationEvent.model.entity;

import br.com.convoquei.backend._shared.model.entity.BaseEntity;
import br.com.convoquei.backend.organization.model.entity.Organization;
import br.com.convoquei.backend.organizationEvent.model.enums.OrganizationEventAssignmentModeEnum;
import br.com.convoquei.backend.organizationEvent.model.enums.OrganizationEventStatusEnum;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organization_event")
public class OrganizationEvent extends BaseEntity {

    protected OrganizationEvent() {
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_event_organization")
    )
    private Organization organization;

    @Column(name = "name", nullable = false, length = 70)
    private String name;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "location", nullable = false, length = 250)
    private String location;

    @Column(name = "start_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    public OffsetDateTime startAt;

    @Column(name = "end_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    public OffsetDateTime endAt;

    @Column(name = "availability_lock_before_start_in_minutes", nullable = false)
    public int availabilityLockBeforeStartInMinutes;

    @Column(name = "allow_assignments", nullable = false)
    public boolean allowAssignments;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_mode", nullable = false, length = 30)
    public OrganizationEventAssignmentModeEnum assignmentMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    public OrganizationEventStatusEnum status;

    @OneToMany(
            mappedBy = "event",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrganizationEventParticipant> participants = new ArrayList<>();

}
