package br.com.convoquei.backend.core.organization.organization.entity;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import br.com.convoquei.backend.core.organization.group.entity.OrganizationGroup;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.model.entity.BaseEntity;
import br.com.convoquei.backend.core.organization.organization.enums.OrganizationStatus;
import br.com.convoquei.backend.core.organization.invite.entity.OrganizationInvite;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMemberRole;
import br.com.convoquei.backend.core.organization.role.entity.OrganizationRole;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationDefaultSystemRole;
import br.com.convoquei.backend.core.user.entity.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "organization",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_organization_slug", columnNames = "slug")
        }
)
public class Organization extends BaseEntity {
    protected Organization() {
    }

    public Organization(String name, String slug, User owner) {
        this.name = name;
        this.slug = slug;
        this.status = OrganizationStatus.ACTIVE;
        this.photoUrl = "";

        for (OrganizationDefaultSystemRole defaultRole : OrganizationDefaultSystemRole.values()) {
            this.roles.add(OrganizationRole.createSystemRole(this, defaultRole));
        }

        addOwner(owner);
    }

    @Column(name = "name", nullable = false, length = 70)
    private String name;

    @Column(name = "slug", nullable = false, length = 150, unique = true)
    private String slug;

    @Column(name = "photo_url", columnDefinition = "text", nullable = false)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrganizationStatus status;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrganizationMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrganizationRole> roles = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrganizationMemberRole> memberRoles = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrganizationInvite> invites = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrganizationGroup> groups = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrganizationFunction> functions = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrganizationEvent> events = new ArrayList<>();

    public OrganizationInvite createInvite(OrganizationMember member, String email) {
        if (!member.getOrganization().getId().equals(this.getId()))
            throw new DomainBusinessRuleException("O membro especificado não pertence à organização para criar um convite.");

        OrganizationInvite invite = new OrganizationInvite(this, member, email);

        this.invites.add(invite);

        return invite;
    }

    public List<OrganizationMember> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public OrganizationStatus getStatus() {
        return status;
    }

    public List<OrganizationRole> getRoles() {
        return roles;
    }

    public List<OrganizationGroup> getGroups() {
        return groups;
    }

    public List<OrganizationFunction> getFunctions() {
        return functions;
    }

    private void addOwner(User user) {
        OrganizationMember member = addMember(user);

        OrganizationRole ownerRole = this.roles.stream()
                .filter(OrganizationRole::isOwner)
                .findFirst()
                .orElseThrow(() -> new DomainBusinessRuleException("A organização não possui uma função de proprietário definida."));

        member.addRole(ownerRole);
    }

    public OrganizationMember addMember(User user) {
        if (this.members.stream().anyMatch(m -> m.getUser().getId().equals(user.getId())))
            throw new DomainBusinessRuleException("O usuário especificado já é membro da organização.");

        OrganizationMember member = new OrganizationMember(this, user);
        this.members.add(member);

        return member;
    }
}
