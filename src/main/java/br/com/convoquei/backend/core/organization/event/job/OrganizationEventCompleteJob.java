package br.com.convoquei.backend.core.organization.event.job;

import br.com.convoquei.backend.app.config.quartz.ConvoqueiJob;
import br.com.convoquei.backend.app.config.quartz.ConvoqueiJobHandler;
import br.com.convoquei.backend.core.organization.event.job.handlers.OrganizationEventCompleteHandler;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrganizationEventCompleteJob extends ConvoqueiJob {
    private final OrganizationEventCompleteHandler handler;

    public OrganizationEventCompleteJob(MeterRegistry meterRegistry, OrganizationEventCompleteHandler handler) {
        super(meterRegistry);
        this.handler = handler;
    }

    @Override
    protected String jobName() {
        return "organization-event-complete-job";
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
