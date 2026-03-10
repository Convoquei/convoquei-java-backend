package br.com.convoquei.backend.core.organization.event.job;

import br.com.convoquei.backend.app.config.quartz.ConvoqueiJob;
import br.com.convoquei.backend.app.config.quartz.ConvoqueiJobHandler;

public class OrganizationEventAvailabilityDeadlineJob extends ConvoqueiJob {
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
        return null;
    }
}
