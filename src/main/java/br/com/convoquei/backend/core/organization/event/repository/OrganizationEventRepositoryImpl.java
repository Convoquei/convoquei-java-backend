package br.com.convoquei.backend.core.organization.event.repository;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEventCalledFunction;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEventParticipant;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentMode;
import br.com.convoquei.backend.core.organization.event.repository.queries.FindMemberEventsQuery;
import br.com.convoquei.backend.core.organization.event.repository.views.OrganizationEventCalledFunctionView;
import br.com.convoquei.backend.core.organization.event.repository.views.OrganizationEventMemberView;
import br.com.convoquei.backend.core.organization.function.entity.OrganizationFunction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

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

        Join<OrganizationEvent, OrganizationEventParticipant> memberParticipant =
                joinMemberParticipant(cb, event, organizationMemberId);

        Join<OrganizationEventParticipant, OrganizationFunction> participantFunction =
                memberParticipant.join("function", JoinType.LEFT);

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
                        cb.count(allParticipants.get("id")),
                        event.get("configuration").get("functionCallMode"),
                        event.get("configuration").get("assignmentMode"),
                        memberParticipant.get("availabilityStatus"),
                        memberParticipant.get("status"),
                        participantFunction.get("id"),
                        participantFunction.get("name"),
                        participantFunction.get("emoji")
                )
        );

        query.where(predicates.toArray(new Predicate[0]));

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
                event.get("configuration").get("functionCallMode"),
                event.get("configuration").get("assignmentMode"),
                memberParticipant.get("availabilityStatus"),
                memberParticipant.get("status"),
                participantFunction.get("id"),
                participantFunction.get("name"),
                participantFunction.get("emoji")
        );

        query.orderBy(cb.asc(event.get("startAt")));

        TypedQuery<OrganizationEventMemberView> typedQuery =
                entityManager.createQuery(query);

        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<OrganizationEventMemberView> content = typedQuery.getResultList();

        if (!content.isEmpty()) {
            List<UUID> eventIds = content.stream()
                    .map(OrganizationEventMemberView::id)
                    .toList();

            Map<UUID, List<OrganizationEventCalledFunctionView>> calledFunctionsMap =
                    findCalledFunctionsMappedByEventId(eventIds);

            content = content.stream()
                    .map(view -> view.withCalledFunctions(
                            calledFunctionsMap.getOrDefault(view.id(), List.of())
                    ))
                    .toList();
        }

        Long total = executeCountQuery(cb, organizationId, organizationMemberId, filters);

        return new PageImpl<>(content, pageable, total);
    }

    private Map<UUID, List<OrganizationEventCalledFunctionView>> findCalledFunctionsMappedByEventId(
            List<UUID> eventIds
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

        Root<OrganizationEventCalledFunction> root =
                query.from(OrganizationEventCalledFunction.class);

        Join<OrganizationEventCalledFunction, OrganizationFunction> function =
                root.join("function", JoinType.LEFT);

        query.multiselect(
                root.get("event").get("id"),
                function.get("id"),
                function.get("name"),
                function.get("emoji"),
                root.get("minMembers"),
                root.get("maxMembers"),
                root.get("assignmentMode")
        );

        query.where(root.get("event").get("id").in(eventIds));

        return entityManager.createQuery(query).getResultList().stream()
                .collect(Collectors.groupingBy(
                        row -> (UUID) row[0],
                        Collectors.mapping(row -> new OrganizationEventCalledFunctionView(
                                (UUID) row[1],
                                (String) row[2],
                                (String) row[3],
                                (Integer) row[4],
                                (Integer) row[5],
                                (OrganizationEventAssignmentMode) row[6]
                        ), Collectors.toList())
                ));
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