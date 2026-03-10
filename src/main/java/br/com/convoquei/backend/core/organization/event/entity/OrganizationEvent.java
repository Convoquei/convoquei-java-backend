package br.com.convoquei.backend.core.organization.event.entity;

import br.com.convoquei.backend.core.organization.event.enums.*;
import br.com.convoquei.backend.core.organization.event.valueobject.OrganizationEventConfiguration;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import br.com.convoquei.backend.core.organization.group.entity.OrganizationGroup;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.member.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.model.entity.BaseEntity;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.sharedkernel.model.valueobject.RuleResult;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "organization_event")
public class OrganizationEvent extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_organization_event_organization")
    )
    private Organization organization;

    @Embedded
    private OrganizationEventConfiguration configuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "created_by_member_id",
            foreignKey = @ForeignKey(name = "fk_organization_event_created_by_member")
    )
    private OrganizationMember createdBy;

    @Column(name = "start_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime startAt;

    @Column(name = "end_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime endAt;

    @Column(name = "allow_assignments", nullable = false)
    private boolean allowAssignments;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrganizationEventStatus status;

    @OneToMany(
            mappedBy = "event",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<OrganizationEventParticipant> participants = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrganizationEventCalledGroup> calledGroups = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrganizationEventCalledFunction> calledFunctions = new HashSet<>();

    protected OrganizationEvent() {
    }

    public OrganizationEvent(
            Organization organization,
            OrganizationMember createdBy,
            OrganizationEventConfiguration configuration,
            OffsetDateTime startAt,
            OffsetDateTime endAt
    ) {
        if (startAt.isAfter(endAt)) {
            throw new DomainBusinessRuleException("A data de início não pode ser posterior à data de término.");
        }

        this.organization = organization;
        this.createdBy = createdBy;
        this.configuration = configuration;
        this.startAt = startAt;
        this.endAt = endAt;
        this.allowAssignments = true;
        this.status = OrganizationEventStatus.SCHEDULED;
    }

    public Organization getOrganization() {
        return organization;
    }

    public OrganizationEventConfiguration getConfiguration() {
        return configuration;
    }

    public OrganizationEventStatus getStatus() {
        return status;
    }

    public OffsetDateTime getStartAt() {
        return startAt;
    }

    public OffsetDateTime getEndAt() {
        return endAt;
    }

    public boolean isAllowAssignments() {
        return allowAssignments;
    }

    public Set<OrganizationEventParticipant> getParticipants() {
        return participants;
    }

    public Set<OrganizationEventCalledGroup> getCalledGroups() {
        return calledGroups;
    }

    public Set<OrganizationEventCalledFunction> getCalledFunctions() {
        return calledFunctions;
    }

    public void addCalledGroups(OrganizationGroup group) {

        if (configuration.getCallMode() != OrganizationEventCallMode.ONLY_SPECIFIED_GROUP_MEMBERS) {
            throw new DomainBusinessRuleException(
                    "Este evento não está configurado para convocar apenas membros de grupos específicos. O modo atual é: "
                            + configuration.getCallMode().getTitle()
            );
        }

        OrganizationEventCalledGroup groupCalledCreated = new OrganizationEventCalledGroup(this, group);

        calledGroups.add(groupCalledCreated);
    }

    public void addCalledFunctions(OrganizationFunction function) {

        if (configuration.getFunctionCallMode() != OrganizationEventFunctionCallMode.ONLY_MEMBERS_WITH_LISTED_FUNCTIONS) {
            throw new DomainBusinessRuleException(
                    "Este evento não está configurado para convocar apenas membros de funções específicas. O modo atual é: "
                            + configuration.getFunctionCallMode().getTitle()
            );
        }
        OrganizationEventCalledFunction functionCalledCreated = new OrganizationEventCalledFunction(this, function);


        calledFunctions.add(functionCalledCreated);
    }

    public void addParticipant(OrganizationMember member) {

        RuleResult result = canMemberParticipate(member);

        if (!result.success()) {
            throw new DomainBusinessRuleException(result.message());
        }

        OrganizationEventParticipant participant =
                new OrganizationEventParticipant(
                        this,
                        member,
                        member.getUser().getName(),
                        member.getUser().getAvatarUrl()
                );

        participants.add(participant);
    }

    public void removeParticipant(OrganizationMember member) {

        boolean removed = participants.removeIf(p ->
                p.getMember().getId().equals(member.getId())
        );

        if (!removed) {
            throw new DomainBusinessRuleException("Participante não encontrado neste evento.");
        }
    }

    public RuleResult canMemberParticipate(OrganizationMember member) {

        if (member.getStatus() != OrganizationMemberStatus.ACTIVE)
            return RuleResult.failure("Não é possível participar do evento pois não está ativo na organização.");

        if (configuration.getCallMode() == OrganizationEventCallMode.ONLY_SPECIFIED_GROUP_MEMBERS) {

            boolean allowed = calledGroups.stream()
                    .map(OrganizationEventCalledGroup::getGroup)
                    .anyMatch(group -> group.hasMemberId(member.getId()));

            if (!allowed) {
                return RuleResult.failure(
                        "Você não pode participar deste evento porque não pertence a nenhum dos grupos convocados."
                );
            }
        }

        if (configuration.getFunctionCallMode() == OrganizationEventFunctionCallMode.ONLY_MEMBERS_WITH_LISTED_FUNCTIONS) {

            boolean allowed = calledFunctions.stream()
                    .map(OrganizationEventCalledFunction::getFunction)
                    .anyMatch(function -> function.hasMemberId(member.getId()));

            if (!allowed) {
                return RuleResult.failure(
                        "Você não pode participar deste evento porque não possui nenhuma das funções convocadas."
                );
            }
        }

        return RuleResult.ok();
    }

    public Optional<OrganizationEventParticipant> findParticipantByMemberId(UUID memberId) {
        return participants.stream()
                .filter(p -> memberId.equals(p.getMember().getId()))
                .findFirst();
    }

    public OffsetDateTime getAvailabilityLockAt() {
        return startAt.minusMinutes(configuration.getAvailabilityLockBeforeStartInMinutes());
    }

    public Duration getTimeUntilAvailabilityLock() {
        Duration remaining = Duration.between(
                OffsetDateTime.now(),
                getAvailabilityLockAt()
        );

        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    public OrganizationEventAssignmentMode getAssignmentMode() {
        return configuration.getAssignmentMode();
    }

    public boolean isAvailabilityLocked() {
        return !getTimeUntilAvailabilityLock().isZero();
    }

    public void lockAvailability() {
        if (status == OrganizationEventStatus.CANCELED) {
            throw new DomainBusinessRuleException("Não é possível trancar a disponibilidade de um evento cancelado.");
        }
        if (status != OrganizationEventStatus.SCHEDULED) {
            throw new DomainBusinessRuleException("Somente eventos com status SCHEDULED podem ter a disponibilidade travada.");
        }
        if (!allowAssignments) {
            return;
        }

        this.allowAssignments = false;

        markParticipantsAsResponseExpired();
    }

    public void start() {
        if (status == OrganizationEventStatus.CANCELED) {
            throw new DomainBusinessRuleException("Não é possível iniciar um evento que foi cancelado.");
        }
        if (status != OrganizationEventStatus.SCHEDULED) {
            throw new DomainBusinessRuleException("Somente eventos com status SCHEDULED podem ser iniciados.");
        }

        this.allowAssignments = false;
        this.status = OrganizationEventStatus.IN_PROGRESS;

        markParticipantsAsNotAssigned();
    }

    public void complete() {
        if (status == OrganizationEventStatus.CANCELED) {
            throw new DomainBusinessRuleException("Não é possível finalizar um evento que foi cancelado.");
        }
        if (status == OrganizationEventStatus.COMPLETED) {
            throw new DomainBusinessRuleException("Este evento já está finalizado.");
        }

        this.allowAssignments = false;
        this.status = OrganizationEventStatus.COMPLETED;

        markParticipantsAsNotAssigned();
    }

    public void respondAvailability(UUID memberId, boolean available) {
        if (!allowAssignments) {
            throw new DomainBusinessRuleException("Este evento não está aceitando respostas de disponibilidade no momento.");
        }

        if (status != OrganizationEventStatus.SCHEDULED) {
            throw new DomainBusinessRuleException("Somente eventos agendados podem aceitar respostas de disponibilidade.");
        }

        OrganizationEventParticipant participant = findParticipantByMemberId(memberId)
                .orElseThrow(() -> new DomainBusinessRuleException("Você não é participante deste evento."));

        participant.markAvailability(available);

        processAutomaticAssignmentsIfNeeded(participant);
    }

    public void clearAvailability(UUID memberId) {
        if (!allowAssignments) {
            throw new DomainBusinessRuleException("Este evento não está aceitando respostas de disponibilidade no momento.");
        }

        OrganizationEventParticipant participant = findParticipantByMemberId(memberId)
                .orElseThrow(() -> new DomainBusinessRuleException("Você não é participante deste evento."));

        participant.clearAvailability();

        processAutomaticAssignmentsIfNeeded(participant);
    }

    private void markParticipantsAsNotAssigned() {
        this.participants.stream()
                .filter(p -> p.getStatus() != OrganizationEventParticipantStatus.ASSIGNED)
                .forEach(p -> p.setStatus(OrganizationEventParticipantStatus.NOT_ASSIGNED));
    }

    private void markParticipantsAsResponseExpired() {
        this.participants.stream()
                .filter(p -> p.getStatus() == OrganizationEventParticipantStatus.PENDING)
                .forEach(p -> p.setStatus(OrganizationEventParticipantStatus.EXPIRED));
    }

    private void processAutomaticAssignmentsIfNeeded(OrganizationEventParticipant participant) {
        if (configuration.getAssignmentMode() != OrganizationEventAssignmentMode.AUTOMATIC) {
            return;
        }

        if (participant.getAvailabilityStatus() == OrganizationEventParticipantAvailabilityStatus.AVAILABLE) {
            participant.setStatus(OrganizationEventParticipantStatus.ASSIGNED);
        } else {
            participant.setStatus(OrganizationEventParticipantStatus.PENDING);
        }
    }
}