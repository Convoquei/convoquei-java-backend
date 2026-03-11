package br.com.convoquei.backend.core.organization.event.events.handlers;

import br.com.convoquei.backend.core.organization.event.events.OrganizationEventLockAvailabilityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrganizationEventLockAvailabilityEventHandler {

    private static final Logger log = LoggerFactory.getLogger(OrganizationEventLockAvailabilityEventHandler.class);

    @TransactionalEventListener
    public void handle(OrganizationEventLockAvailabilityEvent event) {
        log.info("Evento {} teve sua disponibilidade travada", event.event().getId());
    }
}
