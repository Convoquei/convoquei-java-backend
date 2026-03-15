package br.com.convoquei.backend.core.organization.function.entity;

import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.model.entity.BaseEntity;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organization_function")
public class OrganizationFunction extends BaseEntity {

    protected OrganizationFunction() {
    }

    public OrganizationFunction(Organization organization, String name, String emoji) {
        this.organization = organization;
        this.name = name;
        this.emoji = emoji;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_function_organization")
    )
    private Organization organization;

    @Column(name = "name", nullable = false, length = 70)
    private String name;

    @Column(name = "emoji", length = 10)
    private String emoji;

    @OneToMany(mappedBy = "organizationFunction", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrganizationFunctionMember> members = new ArrayList<>();

    public void clearMembers() {
        this.members.clear();
    }

    public Organization getOrganization() {
        return organization;
    }

    public String getName() {
        return name;
    }

    public String getEmoji() {
        return emoji;
    }

    public List<OrganizationFunctionMember> getMembers() {
        return members;
    }

    public void addMember(OrganizationMember member) {
        boolean alreadyIn = members.stream()
                .anyMatch(m -> m.getOrganizationMember().getId().equals(member.getId()));

        if (alreadyIn)
            throw new DomainBusinessRuleException("O membro já possui esta função.");

        this.members.add(new OrganizationFunctionMember(this, this.organization, member));
    }

    public void removeMember(UUID functionParticipantI) {
        boolean removed = members.removeIf(m -> m.getId().equals(functionParticipantI));

        if (!removed)
            throw new DomainBusinessRuleException("Membro não encontrado nesta função.");
    }

    public boolean hasMemberId(UUID memberId) {
        return members.stream()
                .anyMatch(m -> m.getOrganizationMember().getId().equals(memberId));
    }
}
