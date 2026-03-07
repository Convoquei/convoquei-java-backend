package br.com.convoquei.backend.core.organization.member.entity;

import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.model.entity.BaseEntity;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.organization.role.entity.OrganizationRole;
import br.com.convoquei.backend.core.organization.member.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationDefaultSystemRole;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;
import br.com.convoquei.backend.core.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Entity
@Table(
        name = "organization_member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_organization_member_organization_user",
                        columnNames = {"organization_id", "user_id"}
                )
        }
)
public class OrganizationMember extends BaseEntity {

    protected OrganizationMember() {
    }

    public OrganizationMember(Organization organization, User user) {
        this.organization = organization;
        this.user = user;
        this.status = OrganizationMemberStatus.ACTIVE;
        this.joinedAt = LocalDateTime.now();
        this.leftAt = null;
        this.roles.add(new OrganizationMemberRole(this, findSystemRole(OrganizationDefaultSystemRole.MEMBER)));
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_member_organization")
    )
    private Organization organization;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_member_user")
    )
    private User user;

    @OneToMany(mappedBy = "organizationMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrganizationMemberRole> roles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private OrganizationMemberStatus status;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    public boolean isOwner() {
        return getPermissions().stream().anyMatch(OrganizationPermission::isOwner);
    }

    @Transient
    public EnumSet<OrganizationPermission> getPermissions() {
        long combinedMask = roles.stream()
                .mapToLong(omr -> omr.getOrganizationRole().getPermissionsMask())
                .reduce(0L, (a, b) -> a | b);

        EnumSet<OrganizationPermission> permissions = EnumSet.noneOf(OrganizationPermission.class);
        for (OrganizationPermission permission : OrganizationPermission.values()) {
            if ((combinedMask & permission.mask()) != 0) {
                permissions.add(permission);
            }
        }

        return permissions;
    }

    public boolean hasPermission(OrganizationPermission permission) {
        if(isOwner())
            return true;

        if(permission == OrganizationPermission.OWNER)
            return false;

        return getPermissions().contains(permission);
    }

    public void addRole(OrganizationRole role) {
        OrganizationMemberRole memberRole = new OrganizationMemberRole(this, role);
        this.roles.add(memberRole);
    }

    private OrganizationRole findSystemRole(OrganizationDefaultSystemRole defaultRole) {
        return organization.getRoles().stream()
                .filter(role -> role.isSystem() && role.getSystemKey().equals(defaultRole.getSystemKey()))
                .findFirst()
                .orElseThrow(() -> new DomainBusinessRuleException("A organização não possui a função do sistema " + defaultRole.getDisplayName() + " definida."));
    }

    public Organization getOrganization() {
        return organization;
    }

    public User getUser() {
        return user;
    }

    public OrganizationMemberStatus getStatus() {
        return status;
    }

    public List<OrganizationMemberRole> getRoles() {
        return roles;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public void markAsAbandoned() {
        status = OrganizationMemberStatus.ABANDONED;
        leftAt = LocalDateTime.now();

        keepOnlySystemMemberRole();
    }

    private void keepOnlySystemMemberRole() {
        var memberKey = OrganizationDefaultSystemRole.MEMBER.getSystemKey();

        roles.removeIf(r -> !memberKey.equals(r.getOrganizationRole().getSystemKey()));
    }
}
