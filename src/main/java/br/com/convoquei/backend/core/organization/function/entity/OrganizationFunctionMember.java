package br.com.convoquei.backend.core.organization.function.entity;

import br.com.convoquei.backend.sharedkernel.model.entity.BaseEntity;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "organization_function_member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_organization_function_member",
                        columnNames = {"organization_function_id", "organization_member_id"}
                )
        }
)
public class OrganizationFunctionMember extends BaseEntity {

    protected OrganizationFunctionMember() {
    }

    public OrganizationFunctionMember(OrganizationFunction organizationFunction, Organization organization, OrganizationMember member) {
        this.organizationFunction = organizationFunction;
        this.organization = organization;
        this.organizationMember = member;
        this.joinedAt = LocalDateTime.now();
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_function_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_function_member_function")
    )
    private OrganizationFunction organizationFunction;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_function_member_organization")
    )
    private Organization organization;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_function_member_member")
    )
    private OrganizationMember organizationMember;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    public OrganizationFunction getOrganizationFunction() {
        return organizationFunction;
    }

    public Organization getOrganization() {
        return organization;
    }

    public OrganizationMember getOrganizationMember() {
        return organizationMember;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
