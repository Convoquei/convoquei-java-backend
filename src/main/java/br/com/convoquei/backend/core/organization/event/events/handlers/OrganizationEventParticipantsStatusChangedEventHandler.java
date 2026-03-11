package br.com.convoquei.backend.core.organization.event.events.handlers;

import br.com.convoquei.backend.core.organization.event.events.OrganizationEventParticipantsStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrganizationEventParticipantsStatusChangedEventHandler {

    private static final Logger log = LoggerFactory.getLogger(OrganizationEventParticipantsStatusChangedEventHandler.class);

    @TransactionalEventListener
    public void handle(OrganizationEventParticipantsStatusChangedEvent event) {
        log.info("Participantes do evento {} tiveram seu status alterado", event.participants().size());
    }
}
