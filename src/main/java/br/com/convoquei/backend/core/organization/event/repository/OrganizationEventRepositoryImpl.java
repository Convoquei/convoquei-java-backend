package br.com.convoquei.backend.core.organization.event.repository;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEventParticipant;
import br.com.convoquei.backend.core.organization.event.repository.queries.FindMemberEventsQuery;
import br.com.convoquei.backend.core.organization.event.repository.views.OrganizationEventMemberView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrganizationEventRepositoryImpl implements OrganizationEventRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<OrganizationEventMemberView> findEventsForMember(
            UUID organizationId,
            UUID organizationMemberId,
            FindMemberEventsQuery filters
    ) {

        Pageable pageable = filters.pageable();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrganizationEventMemberView> query =
                cb.createQuery(OrganizationEventMemberView.class);

        Root<OrganizationEvent> event = query.from(OrganizationEvent.class);

        // JOIN para filtrar/projetar os dados do membro específico
        Join<OrganizationEvent, OrganizationEventParticipant> memberParticipant =
                joinMemberParticipant(cb, event, organizationMemberId);

        // LEFT JOIN em todos os participantes para contagem — sem correlação
        Join<OrganizationEvent, OrganizationEventParticipant> allParticipants =
                event.join("participants", JoinType.LEFT);

        List<Predicate> predicates =
                buildPredicates(cb, event, memberParticipant, organizationId, filters);

        query.select(
                cb.construct(
                        OrganizationEventMemberView.class,
                        event.get("id"),
                        event.get("configuration").get("name"),
                        event.get("configuration").get("description"),
                        event.get("configuration").get("location"),
                        event.get("startAt"),
                        event.get("endAt"),
                        event.get("status"),
                        event.get("configuration").get("availabilityLockBeforeStartInMinutes"),
                        event.get("allowAssignments"),
                        // COUNT via expressão condicional — sem SubPlan
                        cb.count(allParticipants.get("id")),
                        event.get("configuration").get("assignmentMode"),
                        memberParticipant.get("availabilityStatus"),
                        memberParticipant.get("status")
                )
        );

        query.where(predicates.toArray(new Predicate[0]));

        // GROUP BY necessário porque agora usamos COUNT no SELECT
        query.groupBy(
                event.get("id"),
                event.get("configuration").get("name"),
                event.get("configuration").get("description"),
                event.get("configuration").get("location"),
                event.get("startAt"),
                event.get("endAt"),
                event.get("status"),
                event.get("configuration").get("availabilityLockBeforeStartInMinutes"),
                event.get("allowAssignments"),
                event.get("configuration").get("assignmentMode"),
                memberParticipant.get("availabilityStatus"),
                memberParticipant.get("status")
        );

        query.orderBy(cb.asc(event.get("startAt")));

        TypedQuery<OrganizationEventMemberView> typedQuery =
                entityManager.createQuery(query);

        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<OrganizationEventMemberView> content = typedQuery.getResultList();

        Long total = executeCountQuery(cb, organizationId, organizationMemberId, filters);

        return new PageImpl<>(content, pageable, total);
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<OrganizationEvent> event,
            Join<OrganizationEvent, OrganizationEventParticipant> memberParticipant,
            UUID organizationId,
            FindMemberEventsQuery filters
    ) {

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(
                cb.equal(event.get("organization").get("id"), organizationId)
        );

        if (filters.eventNameStartsWith() != null) {
            predicates.add(
                    cb.like(
                            cb.lower(event.get("configuration").get("name")),
                            filters.eventNameStartsWith().toLowerCase() + "%"
                    )
            );
        }

        if (filters.startAfter() != null) {
            predicates.add(
                    cb.greaterThanOrEqualTo(event.get("startAt"), filters.startAfter())
            );
        }

        if (filters.startBefore() != null) {
            predicates.add(
                    cb.lessThan(event.get("startAt"), filters.startBefore())
            );
        }

        if (filters.participantStatus() != null && !filters.participantStatus().isEmpty()) {
            predicates.add(
                    memberParticipant.get("status").in(filters.participantStatus())
            );
        }

        if (filters.eventStatus() != null && !filters.eventStatus().isEmpty()) {
            predicates.add(
                    event.get("status").in(filters.eventStatus())
            );
        }

        return predicates;
    }

    private Long executeCountQuery(
            CriteriaBuilder cb,
            UUID organizationId,
            UUID organizationMemberId,
            FindMemberEventsQuery filters
    ) {

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);

        Root<OrganizationEvent> event =
                countQuery.from(OrganizationEvent.class);

        Join<OrganizationEvent, OrganizationEventParticipant> memberParticipant =
                joinMemberParticipant(cb, event, organizationMemberId);

        List<Predicate> predicates =
                buildPredicates(cb, event, memberParticipant, organizationId, filters);

        countQuery.select(cb.countDistinct(event));
        countQuery.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private Join<OrganizationEvent, OrganizationEventParticipant> joinMemberParticipant(
            CriteriaBuilder cb,
            Root<OrganizationEvent> event,
            UUID organizationMemberId
    ) {

        Join<OrganizationEvent, OrganizationEventParticipant> memberParticipant =
                event.join("participants", JoinType.LEFT);

        memberParticipant.on(
                cb.equal(memberParticipant.get("member").get("id"), organizationMemberId)
        );

        return memberParticipant;
    }
}