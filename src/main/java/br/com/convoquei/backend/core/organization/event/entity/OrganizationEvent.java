package br.com.convoquei.backend.core.organization.event.entity;

import br.com.convoquei.backend.core.organization.event.enums.*;
import br.com.convoquei.backend.core.organization.event.events.OrganizationEventLockAvailabilityEvent;
import br.com.convoquei.backend.core.organization.event.events.OrganizationEventParticipantsStatusChangedEvent;
import br.com.convoquei.backend.core.organization.event.valueobject.OrganizationEventConfiguration;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import br.com.convoquei.backend.core.organization.group.entity.OrganizationGroup;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.member.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.model.entity.BaseEntity;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;
import br.com.convoquei.backend.sharedkernel.model.valueobject.RuleResult;
import br.com.convoquei.backend.core.organization.event.util.OrganizationEventHelper;
import jakarta.persistence.*;
import org.aspectj.weaver.ast.Or;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cancelled_by_member_id",
            foreignKey = @ForeignKey(name = "fk_organization_event_cancelled_by_member")
    )
    private OrganizationMember cancelledBy;

    @Column(name = "start_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime startAt;

    @Column(name = "end_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime endAt;

    @Column(name = "allow_assignments", nullable = false)
    private boolean allowAssignments;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
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

        validateAvailabilityLock(configuration, startAt);

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

    public void update(OrganizationEventConfiguration newConfiguration, OffsetDateTime newStartAt, OffsetDateTime newEndAt) {
        if (status != OrganizationEventStatus.SCHEDULED) {
            throw new DomainBusinessRuleException("Somente eventos agendados podem ser editados.");
        }

        if (newStartAt.isAfter(newEndAt)) {
            throw new DomainBusinessRuleException("A data de início não pode ser posterior à data de término.");
        }

        validateAvailabilityLock(newConfiguration, newStartAt);

        this.configuration = newConfiguration;
        this.startAt = newStartAt;
        this.endAt = newEndAt;
        this.calledGroups.clear();
        this.calledFunctions.clear();
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

    public void addCalledFunctions(OrganizationFunction function, int minMembers, Integer maxMembers, OrganizationEventAssignmentMode assignmentMode) {
        OrganizationEventCalledFunction functionCalledCreated = new OrganizationEventCalledFunction(this, function, minMembers, maxMembers, assignmentMode);


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

    public void syncParticipants() {
        participants.removeIf(p -> !canMemberParticipate(p.getMember()).success());

        organization.getMembers().stream()
                .filter(m -> m.getStatus() == OrganizationMemberStatus.ACTIVE)
                .filter(m -> canMemberParticipate(m).success())
                .filter(m -> findParticipantByMemberId(m.getId()).isEmpty())
                .forEach(this::addParticipant);
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
        return Duration.ofSeconds(OrganizationEventHelper.secondsUntilAvailabilityLock(startAt, configuration.getAvailabilityLockBeforeStartInMinutes()));
    }

    public void lockAvailability() {
        if (status != OrganizationEventStatus.SCHEDULED) {
            throw new DomainBusinessRuleException("Somente eventos agendados podem ter a disponibilidade travada.");
        }
        if (!allowAssignments) {
            return;
        }

        this.allowAssignments = false;
        this.configuration.setAvailabilityLockBeforeStartInMinutes(0);

        processAutomaticAssignmentsParticipants();

        registerEvent(new OrganizationEventLockAvailabilityEvent(this));
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

    public void respondAvailability(OrganizationMember member, boolean available, UUID functionId) {
        if (!allowAssignments) {
            throw new DomainBusinessRuleException("Este evento não está aceitando respostas de disponibilidade no momento.");
        }

        if (status != OrganizationEventStatus.SCHEDULED) {
            throw new DomainBusinessRuleException("Somente eventos agendados podem aceitar respostas de disponibilidade.");
        }

        OrganizationEventParticipant participant = findParticipantByMemberId(member.getId())
                .orElseThrow(() -> new DomainBusinessRuleException("Você não é participante deste evento."));

        participant.markAvailability(available);

        if (available && functionId != null) {
            OrganizationFunction function = calledFunctions.stream()
                    .map(OrganizationEventCalledFunction::getFunction)
                    .filter(f -> f.getId().equals(functionId))
                    .findFirst()
                    .orElseThrow(() -> new DomainBusinessRuleException("A função informada não está entre as funções convocadas para este evento."));

            if (member.getFunctions().stream().noneMatch(f -> f.getOrganizationFunction().getId().equals(functionId))) {
                throw new DomainBusinessRuleException("Você não pode se disponibilizar para esta função porque não a possui atribuída.");
            }

            participant.assignFunction(function);
        } else if (!available) {
            participant.assignFunction(null);
        }

        processAssignmentForParticipant(participant);
    }

    public void clearAvailability(UUID memberId) {
        if (!allowAssignments) {
            throw new DomainBusinessRuleException("Este evento não está aceitando respostas de disponibilidade no momento.");
        }

        OrganizationEventParticipant participant = findParticipantByMemberId(memberId)
                .orElseThrow(() -> new DomainBusinessRuleException("Você não é participante deste evento."));

        participant.clearAvailability();
        participant.assignFunction(null);
    }

    public void processAssignmentForParticipant(OrganizationEventParticipant participant) {
        Map<UUID, OrganizationEventCalledFunction> calledFunctionsByFunctionId = calledFunctions.stream()
                .collect(Collectors.toMap(cf -> cf.getFunction().getId(), cf -> cf));

        processParticipantAssignment(participant, calledFunctionsByFunctionId);
    }

    public void cancel(OrganizationMember cancelledBy) {
        if (status != OrganizationEventStatus.SCHEDULED) {
            throw new DomainBusinessRuleException("Somente eventos agendados podem ser cancelados.");
        }

        this.status = OrganizationEventStatus.CANCELED;
        this.allowAssignments = false;
        this.cancelledBy = cancelledBy;
    }

    public void reopenAssignments() {
        if (status != OrganizationEventStatus.SCHEDULED) {
            throw new DomainBusinessRuleException("Somente eventos agendados podem ter a escala reaberta.");
        }

        this.allowAssignments = true;
        this.configuration = new OrganizationEventConfiguration(
                configuration.getName(),
                configuration.getDescription(),
                configuration.getLocation(),
                configuration.getAssignmentMode(),
                configuration.getCallMode(),
                configuration.getFunctionCallMode(),
                0
        );

        this.participants.stream()
                .filter(p -> p.getStatus() == OrganizationEventParticipantStatus.EXPIRED)
                .forEach(p -> p.setStatus(OrganizationEventParticipantStatus.PENDING));
    }

    public void lockAssignments() {
        if (status != OrganizationEventStatus.SCHEDULED) {
            throw new DomainBusinessRuleException("Somente eventos agendados podem ter a escala travada.");
        }
        if (!allowAssignments) {
            throw new DomainBusinessRuleException("A escala deste evento já está travada.");
        }

        this.allowAssignments = false;
        this.participants.stream()
                .filter(p -> p.getStatus() == OrganizationEventParticipantStatus.PENDING)
                .forEach(p -> p.setStatus(OrganizationEventParticipantStatus.EXPIRED));
    }

    public OrganizationMember getCancelledBy() {
        return cancelledBy;
    }

    private void markParticipantsAsNotAssigned() {
        this.participants.stream()
                .filter(p -> p.getStatus() != OrganizationEventParticipantStatus.ASSIGNED)
                .forEach(p -> p.setStatus(OrganizationEventParticipantStatus.NOT_ASSIGNED));
    }

    private void processAutomaticAssignmentsParticipants() {
        Map<UUID, OrganizationEventCalledFunction> calledFunctionsByFunctionId = calledFunctions.stream()
                .collect(Collectors.toMap(cf -> cf.getFunction().getId(), cf -> cf));

        participants.stream()
                .sorted(Comparator.comparing(OrganizationEventParticipant::getUpdatedAt))
                .forEach(p -> processParticipantAssignment(p, calledFunctionsByFunctionId));
    }

    private void processParticipantAssignment(OrganizationEventParticipant participant, Map<UUID, OrganizationEventCalledFunction> calledFunctionsByFunctionId) {

        if (participant.getAvailabilityStatus() == OrganizationEventParticipantAvailabilityStatus.NOT_ANSWERED) {
            participant.setStatus(OrganizationEventParticipantStatus.EXPIRED);
            return;
        }
        else if(participant.getAvailabilityStatus() == OrganizationEventParticipantAvailabilityStatus.UNAVAILABLE) {
            participant.setStatus(OrganizationEventParticipantStatus.PENDING);
            return;
        }

        OrganizationFunction function = participant.getFunction();

        if (configuration.getFunctionCallMode() != null && function != null) {
            OrganizationEventCalledFunction calledFunction = calledFunctionsByFunctionId.get(function.getId());

            if (calledFunction != null) {
                if (calledFunction.getAssignmentMode() == OrganizationEventAssignmentMode.MANUAL) {
                    participant.setStatus(OrganizationEventParticipantStatus.AWAITING_ASSIGNMENT);
                    return;
                }

                if (assignParticipantWithFunction(participant, calledFunction)) {
                    return;
                }

                participant.setStatus(OrganizationEventParticipantStatus.NOT_ASSIGNED);
                return;
            }
        }

        if (configuration.getAssignmentMode() == OrganizationEventAssignmentMode.AUTOMATIC) {
            participant.setStatus(OrganizationEventParticipantStatus.ASSIGNED);
            return;
        }

        participant.setStatus(OrganizationEventParticipantStatus.AWAITING_ASSIGNMENT);
    }

    private void validateAvailabilityLock(OrganizationEventConfiguration configuration, OffsetDateTime startAt) {
        int minutesLockBeforeStart = configuration.getAvailabilityLockBeforeStartInMinutes();

        if (minutesLockBeforeStart > 0) {
            if (!allowAssignments)
                throw new DomainBusinessRuleException("Não é possível alterar o bloqueio de disponibilidade para este evento porque ele já está com as respostas de disponibilidade travadas.");

            OffsetDateTime lockMoment = startAt.minusMinutes(minutesLockBeforeStart);
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
            OffsetDateTime minimumLockMomentAllowed = now.plusMinutes(5);

            if (lockMoment.isBefore(minimumLockMomentAllowed)) {
                long maxAllowedMinutes = ChronoUnit.MINUTES.between(now, startAt) - 5;

                long days = maxAllowedMinutes / (60 * 24);
                long hours = (maxAllowedMinutes % (60 * 24)) / 60;
                long minutes = maxAllowedMinutes % 60;

                StringBuilder duration = new StringBuilder();
                if (days > 0)    duration.append(days).append(days == 1 ? " dia" : " dias");
                if (hours > 0)   duration.append(!duration.isEmpty() ? ", " : "").append(hours).append(hours == 1 ? " hora" : " horas");
                if (minutes > 0) duration.append(!duration.isEmpty() ? " e " : "").append(minutes).append(minutes == 1 ? " minuto" : " minutos");

                throw new DomainBusinessRuleException(String.format("O máximo permitido para o bloqueio de disponibilidade deste evento é de %s.", duration));
            }
        }
    }

    private boolean assignParticipantWithFunction(OrganizationEventParticipant participant, OrganizationEventCalledFunction calledFunction) {
        int maxMembersAllowed = calledFunction.getMaxMembers() != null ? calledFunction.getMaxMembers() : Integer.MAX_VALUE;

        long totalMembersAlreadyAssignedWithFunction = participants.stream()
                .filter(p -> p.getStatus() == OrganizationEventParticipantStatus.ASSIGNED)
                .filter(p -> p.getFunction() != null && p.getFunction().getId().equals(calledFunction.getFunction().getId()))
                .count();

        if (totalMembersAlreadyAssignedWithFunction >= maxMembersAllowed) {
            return false;
        }

        participant.setStatus(OrganizationEventParticipantStatus.ASSIGNED);
        return true;
    }
}