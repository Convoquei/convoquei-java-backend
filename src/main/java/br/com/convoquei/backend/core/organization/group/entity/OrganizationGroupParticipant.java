package br.com.convoquei.backend.core.organization.group.entity;

import br.com.convoquei.backend.sharedkernel.model.entity.BaseEntity;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "organization_group_participant",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_organization_group_participant",
                        columnNames = {"organization_group_id", "organization_member_id"}
                )
        }
)
public class OrganizationGroupParticipant extends BaseEntity {

    protected OrganizationGroupParticipant() {
    }

    public OrganizationGroupParticipant(OrganizationGroup organizationGroup, Organization organization, OrganizationMember member) {
        this.organizationGroup = organizationGroup;
        this.organization = organization;
        this.organizationMember = member;
        this.joinedAt = Instant.now();
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_group_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_group_participant_group")
    )
    private OrganizationGroup organizationGroup;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_group_participant_organization")
    )
    private Organization organization;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_group_participant_member")
    )
    private OrganizationMember organizationMember;

    @Column(name = "joined_at")
    private Instant joinedAt;

    public OrganizationGroup getOrganizationGroup() {
        return organizationGroup;
    }

    public Organization getOrganization() {
        return organization;
    }

    public OrganizationMember getOrganizationMember() {
        return organizationMember;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }
}

