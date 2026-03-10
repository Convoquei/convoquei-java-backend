package br.com.convoquei.backend.core.organization.event.job;

import br.com.convoquei.backend.app.config.quartz.ConvoqueiJob;
import br.com.convoquei.backend.app.config.quartz.ConvoqueiJobHandler;
import br.com.convoquei.backend.core.organization.event.handlers.OrganizationEventStartHandler;
import org.springframework.stereotype.Component;

@Component
public class OrganizationEventStartJob extends ConvoqueiJob {
    private final OrganizationEventStartHandler handler;

    public OrganizationEventStartJob(OrganizationEventStartHandler handler) {
        this.handler = handler;
    }

    @Override
    protected String jobName() {
        return "organization-event-start-job";
    }

    @Override
    protected String cron() {
        return "0 0/1 * * * ?";
    }

    @Override
    protected ConvoqueiJobHandler handler() {
        return handler;
    }
}
