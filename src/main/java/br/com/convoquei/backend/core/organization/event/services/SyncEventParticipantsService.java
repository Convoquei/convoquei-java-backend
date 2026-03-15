package br.com.convoquei.backend.core.organization.event.services;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;
import br.com.convoquei.backend.core.organization.event.repository.OrganizationEventRepository;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SyncEventParticipantsService {

    private final OrganizationEventRepository organizationEventRepository;

    public SyncEventParticipantsService(OrganizationEventRepository organizationEventRepository) {
        this.organizationEventRepository = organizationEventRepository;
    }

    @Transactional
    public void execute(UUID organizationId) {
        List<OrganizationEvent> scheduledEvents = organizationEventRepository
                .findAllByOrganizationIdAndStatus(organizationId, OrganizationEventStatus.SCHEDULED);

        scheduledEvents.forEach(OrganizationEvent::syncParticipants);

        organizationEventRepository.saveAll(scheduledEvents);
    }

    @Transactional
    public void executeForMember(UUID organizationId, OrganizationMember member) {
        List<OrganizationEvent> scheduledEvents = organizationEventRepository
                .findAllByOrganizationIdAndStatus(organizationId, OrganizationEventStatus.SCHEDULED);

        scheduledEvents.forEach(event -> syncMemberInEvent(event, member));

        organizationEventRepository.saveAll(scheduledEvents);
    }

    @Transactional
    public void executeForMembers(UUID organizationId, List<OrganizationMember> members) {
        if (members.isEmpty()) return;

        List<OrganizationEvent> scheduledEvents = organizationEventRepository
                .findAllByOrganizationIdAndStatus(organizationId, OrganizationEventStatus.SCHEDULED);

        scheduledEvents.forEach(event -> members.forEach(member -> syncMemberInEvent(event, member)));

        organizationEventRepository.saveAll(scheduledEvents);
    }

    private void syncMemberInEvent(OrganizationEvent event, OrganizationMember member) {
        boolean canParticipate = event.canMemberParticipate(member).success();
        boolean isParticipant = event.findParticipantByMemberId(member.getId()).isPresent();

        if (canParticipate && !isParticipant) {
            event.addParticipant(member);
        } else if (!canParticipate && isParticipant) {
            event.removeParticipant(member);
        }
    }
}
