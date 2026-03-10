package br.com.convoquei.backend.app.config.quartz;

import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ConvoqueiJobRegister {

    private static final Logger log = LoggerFactory.getLogger(ConvoqueiJobRegister.class);
    private final Scheduler scheduler;
    private final List<ConvoqueiJob> jobs;

    public ConvoqueiJobRegister(Scheduler scheduler, List<ConvoqueiJob> jobs) {
        this.scheduler = scheduler;
        this.jobs = jobs;
    }

    @PostConstruct
    public void registerJobs() {

        for (ConvoqueiJob job : jobs) {

            try {

                JobDetail jobDetail = JobBuilder
                        .newJob(job.getClass())
                        .withIdentity(job.jobName())
                        .storeDurably()
                        .build();

                Trigger trigger = TriggerBuilder
                        .newTrigger()
                        .forJob(jobDetail)
                        .withIdentity(job.jobName() + "-trigger")
                        .withSchedule(
                                CronScheduleBuilder.cronSchedule(job.cron())
                        )
                        .build();

                scheduler.scheduleJob(jobDetail, trigger);

                log.info("Job {} registrada com cron: {}", job.jobName(), job.cron());

            } catch (SchedulerException e) {
                throw new RuntimeException("Erro ao registrar job: " + job.jobName(), e);
            }

        }
    }
}