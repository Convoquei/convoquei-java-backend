package br.com.convoquei.backend.core.organization.event.entity;

import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import br.com.convoquei.backend.sharedkernel.model.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
public class OrganizationEventCalledFunction extends BaseEntity {

    protected OrganizationEventCalledFunction() {
    }

    public OrganizationEventCalledFunction(OrganizationEvent event, OrganizationFunction function) {
        this.event = event;
        this.function = function;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "event_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_event_participant_function_event")
    )
    private OrganizationEvent event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "function_id")
    private OrganizationFunction function;

    public OrganizationEvent getEvent() {
        return event;
    }

    public OrganizationFunction getFunction() {
        return function;
    }
}
