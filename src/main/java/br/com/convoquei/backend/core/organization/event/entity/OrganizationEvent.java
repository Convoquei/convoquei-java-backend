package br.com.convoquei.backend.core.organization.event.entity;

import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.model.entity.BaseEntity;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentModeEnum;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatusEnum;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organization_event")
public class OrganizationEvent extends BaseEntity {

    protected OrganizationEvent() {
    }

    public OrganizationEvent(Organization organization, OrganizationMember createdBy, String name, String description, String location, LocalDateTime startAt, LocalDateTime endAt, int availabilityLockBeforeStartInMinutes, OrganizationEventAssignmentModeEnum assignmentMode) {
        this.organization = organization;
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.location = location;
        this.startAt = startAt;
        this.endAt = endAt;
        this.allowAssignments = true;
        this.status = OrganizationEventStatusEnum.SCHEDULED;

        configureAssignment(assignmentMode, availabilityLockBeforeStartInMinutes);
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_event_organization")
    )
    private Organization organization;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "created_by_member_id",
            nullable = true,
            foreignKey = @ForeignKey(name = "fk_organization_event_created_by_member")
    )
    private OrganizationMember createdBy;

    @Column(name = "name", nullable = false, length = 70)
    private String name;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "location", nullable = false, length = 250)
    private String location;

    @Column(name = "start_at", nullable = false)
    public LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    public LocalDateTime endAt;

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

    public void configureAssignment(OrganizationEventAssignmentModeEnum assignmentMode, int availabilityLockBeforeStartInMinutes) {
        if (assignmentMode == OrganizationEventAssignmentModeEnum.AUTOMATIC) {
            if (availabilityLockBeforeStartInMinutes <= 0) {
                throw new DomainBusinessRuleException("O tempo de fechamento automático da escala deve ser informado quando o modo de escalação for automático.");
            }
        }

        this.assignmentMode = assignmentMode;
        this.availabilityLockBeforeStartInMinutes = availabilityLockBeforeStartInMinutes;
    }

    public void toggleAllowAssignments() {
        this.allowAssignments = !this.allowAssignments;
    }

    public void updateStatus(OrganizationEventStatusEnum newStatus) {
        this.status = newStatus;
    }

    public Organization getOrganization() {
        return organization;
    }

    public OrganizationMember getCreatedBy() {
        return createdBy;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public int getAvailabilityLockBeforeStartInMinutes() {
        return availabilityLockBeforeStartInMinutes;
    }

    public boolean isAllowAssignments() {
        return allowAssignments;
    }

    public OrganizationEventAssignmentModeEnum getAssignmentMode() {
        return assignmentMode;
    }

    public OrganizationEventStatusEnum getStatus() {
        return status;
    }

    public List<OrganizationEventParticipant> getParticipants() {
        return participants;
    }
}
