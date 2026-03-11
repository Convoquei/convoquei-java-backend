package br.com.convoquei.backend.app.config.quartz;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.jspecify.annotations.NonNull;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.Duration;
import java.time.Instant;

public abstract class ConvoqueiJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(ConvoqueiJob.class);

    protected final MeterRegistry meterRegistry;

    protected ConvoqueiJob(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    protected abstract String jobName();

    protected abstract String cron();

    protected abstract ConvoqueiJobHandler handler();

    @Override
    protected final void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {

        Instant startTime = Instant.now();

        Timer.Sample sample = Timer.start(meterRegistry);

        log.info("Job {} iniciada em {}", jobName(), startTime);

        try {

            handler().handle();

            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);

            log.info(
                    "Job {} finalizada em {} (tempo de execucao: {} ms)",
                    jobName(),
                    endTime,
                    duration.toMillis()
            );

        } catch (Exception ex) {

            Instant errorTime = Instant.now();
            Duration duration = Duration.between(startTime, errorTime);

            log.error(
                    "Job {} falhou em {} (tempo de execucao: {} ms)",
                    jobName(),
                    errorTime,
                    duration.toMillis(),
                    ex
            );

            throw new JobExecutionException(ex);

        } finally {

            sample.stop(
                    Timer.builder("convoquei.job.execution")
                            .tag("job", jobName())
                            .register(meterRegistry)
            );
        }
    }
}