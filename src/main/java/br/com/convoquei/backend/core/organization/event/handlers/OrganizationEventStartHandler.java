package br.com.convoquei.backend.core.organization.event.handlers;

import br.com.convoquei.backend.app.config.quartz.ConvoqueiJobHandler;
import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventStatus;
import br.com.convoquei.backend.core.organization.event.repository.OrganizationEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class OrganizationEventStartHandler implements ConvoqueiJobHandler {

    private static final Logger log = LoggerFactory.getLogger(OrganizationEventStartHandler.class);
    private final OrganizationEventRepository organizationEventRepository;

    public OrganizationEventStartHandler(OrganizationEventRepository organizationEventRepository) {
        this.organizationEventRepository = organizationEventRepository;
    }

    @Override
    @Transactional
    public void handle() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        List<OrganizationEvent> events = organizationEventRepository.findByStatusAndStartAtLessThanEqualAndEndAtGreaterThan(OrganizationEventStatus.SCHEDULED, now, now);

        for (OrganizationEvent event : events) {
            startEvent(event);
            organizationEventRepository.save(event);
        }

        if (!events.isEmpty()) {
            log.info("Processados {} eventos para início", events.size());
        }
    }

    private void startEvent(OrganizationEvent event) {
        try {
            event.start();
        }
        catch (Exception ex) {
            log.error("Falha ao iniciar evento {}: {}", event.getId(), ex.getMessage(), ex);
        }
    }
}
