package br.com.convoquei.backend.core.organization.event.job.handlers;

import br.com.convoquei.backend.app.config.quartz.ConvoqueiJobHandler;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.repository.OrganizationEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class OrganizationEventAvailabilityDeadlineHandler implements ConvoqueiJobHandler {

    private static final Logger log = LoggerFactory.getLogger(OrganizationEventAvailabilityDeadlineHandler.class);
    private final OrganizationEventRepository organizationEventRepository;

    public OrganizationEventAvailabilityDeadlineHandler(OrganizationEventRepository organizationEventRepository) {
        this.organizationEventRepository = organizationEventRepository;
    }

    @Override
    @Transactional
    public void handle() {

        OffsetDateTime now = OffsetDateTime.now();

        List<OrganizationEvent> events = organizationEventRepository.findScheduledEventsWithExpiredAvailabilityLock(now);

        for (OrganizationEvent event : events) {
            lockAvailability(event);
        }

        if (!events.isEmpty()) {
            log.info("Processados {} eventos para travamento de disponibilidade", events.size());
        }
    }

    private void lockAvailability(OrganizationEvent event) {
        try {
            event.lockAvailability();
            organizationEventRepository.save(event);
        } catch (Exception ex) {
            log.error("Falha ao travar disponibilidade do evento {}: {}", event.getId(), ex.getMessage(), ex);
        }
    }
}

