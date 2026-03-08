package br.com.convoquei.backend.core.organization.event.entity;

import br.com.convoquei.backend.core.organization.group.entity.OrganizationGroup;
import br.com.convoquei.backend.sharedkernel.model.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
public class OrganizationEventCalledGroup extends BaseEntity {

    protected OrganizationEventCalledGroup() {

    }

    public OrganizationEventCalledGroup(OrganizationEvent event, OrganizationGroup group) {
        this.event = event;
        this.group = group;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "event_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_event_participant_group_event")
    )
    private OrganizationEvent event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "group_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_event_participant_group_group")
    )
    private OrganizationGroup group;

    public OrganizationEvent getEvent() {
        return event;
    }

    public OrganizationGroup getGroup() {
        return group;
    }
}
