package br.com.convoquei.backend.core.organization.event.job;

import br.com.convoquei.backend.app.config.quartz.ConvoqueiJob;
import br.com.convoquei.backend.app.config.quartz.ConvoqueiJobHandler;
import br.com.convoquei.backend.core.organization.event.job.handlers.OrganizationEventAvailabilityDeadlineHandler;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrganizationEventAvailabilityDeadlineJob extends ConvoqueiJob {

    private final OrganizationEventAvailabilityDeadlineHandler handler;

    public OrganizationEventAvailabilityDeadlineJob(MeterRegistry meterRegistry, OrganizationEventAvailabilityDeadlineHandler handler) {
        super(meterRegistry);
        this.handler = handler;
    }

    @Override
    protected String jobName() {
        return "organization-event-availability-deadline-job";
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
