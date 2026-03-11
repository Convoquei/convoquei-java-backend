package br.com.convoquei.backend.core.organization.event.job.handlers;

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
public class OrganizationEventCompleteHandler implements ConvoqueiJobHandler {

    private static final Logger log = LoggerFactory.getLogger(OrganizationEventCompleteHandler.class);
    private final OrganizationEventRepository organizationEventRepository;

    public OrganizationEventCompleteHandler(OrganizationEventRepository organizationEventRepository) {
        this.organizationEventRepository = organizationEventRepository;
    }

    @Override
    @Transactional
    public void handle() {

        OffsetDateTime now = OffsetDateTime.now();

        List<OrganizationEvent> events =
                organizationEventRepository.findByStatusNotInAndEndAtLessThanEqual(
                        List.of(
                                OrganizationEventStatus.CANCELED,
                                OrganizationEventStatus.COMPLETED
                        ),
                        now
                );

        for (OrganizationEvent event : events) {
            completeEvent(event);
        }

        if (!events.isEmpty()) {
            log.info("Processados {} eventos para finalização", events.size());
        }
    }

    private void completeEvent(OrganizationEvent event) {
        try {
            event.complete();
            organizationEventRepository.save(event);
        } catch (Exception ex) {
            log.error("Falha ao finalizar evento {}: {}", event.getId(), ex.getMessage(), ex);
        }
    }
}