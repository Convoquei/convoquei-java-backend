package br.com.convoquei.backend.core.organization.event.entity;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentMode;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.model.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
public class OrganizationEventCalledFunction extends BaseEntity {

    protected OrganizationEventCalledFunction() {
    }

    public OrganizationEventCalledFunction(OrganizationEvent event, OrganizationFunction function, int minMembers, Integer maxMembers, OrganizationEventAssignmentMode assignmentMode) {
        if (minMembers < 1) {
            throw new DomainBusinessRuleException("O número mínimo de membros deve ser maior ou igual a um.");
        }
        if (maxMembers != null && maxMembers < minMembers) {
            throw new DomainBusinessRuleException("O número máximo de membros deve ser maior ou igual ao número mínimo de membros.");
        }

        this.event = event;
        this.function = function;
        this.minMembers = minMembers;
        this.maxMembers = maxMembers;
        this.assignmentMode = assignmentMode;
    }

    @Column(name = "min_members", nullable = false)
    private int minMembers;

    @Column(name = "max_members")
    private Integer maxMembers;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_mode", nullable = false, length = 50)
    private OrganizationEventAssignmentMode assignmentMode;

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

    public int getMinMembers() {
        return minMembers;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public OrganizationEventAssignmentMode getAssignmentMode() {
        return assignmentMode;
    }

    public OrganizationEvent getEvent() {
        return event;
    }

    public OrganizationFunction getFunction() {
        return function;
    }
}