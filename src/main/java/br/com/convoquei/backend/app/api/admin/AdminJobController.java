package br.com.convoquei.backend.app.api.admin;

import br.com.convoquei.backend.app.api.admin.dto.JobInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/jobs")
@Tag(name = "Admin - Jobs", description = "Gerenciamento dos jobs agendados")
public class AdminJobController {

    private final Scheduler scheduler;

    public AdminJobController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @GetMapping
    @Operation(summary = "Listar todos os jobs com status e próxima execução")
    public List<JobInfoResponse> listJobs() throws SchedulerException {
        List<JobInfoResponse> result = new ArrayList<>();

        for (JobKey key : scheduler.getJobKeys(GroupMatcher.anyGroup())) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(key);

            if (triggers.isEmpty()) {
                result.add(new JobInfoResponse(key.getName(), key.getGroup(), null, "SEM_TRIGGER", null, null));
                continue;
            }

            Trigger trigger = triggers.get(0);
            Trigger.TriggerState state = scheduler.getTriggerState(trigger.getKey());
            String cron = trigger instanceof CronTrigger ct ? ct.getCronExpression() : null;

            result.add(new JobInfoResponse(
                    key.getName(),
                    key.getGroup(),
                    cron,
                    state.name(),
                    trigger.getPreviousFireTime() != null ? trigger.getPreviousFireTime().toInstant() : null,
                    trigger.getNextFireTime() != null ? trigger.getNextFireTime().toInstant() : null
            ));
        }

        return result;
    }

    @PostMapping("/{name}/trigger")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Disparar um job manualmente")
    public void triggerJob(@PathVariable String name) throws SchedulerException {
        JobKey key = resolveJobKey(name);
        scheduler.triggerJob(key);
    }

    @PostMapping("/{name}/pause")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Pausar um job")
    public void pauseJob(@PathVariable String name) throws SchedulerException {
        JobKey key = resolveJobKey(name);
        scheduler.pauseJob(key);
    }

    @PostMapping("/{name}/resume")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Retomar um job pausado")
    public void resumeJob(@PathVariable String name) throws SchedulerException {
        JobKey key = resolveJobKey(name);
        scheduler.resumeJob(key);
    }

    private JobKey resolveJobKey(String name) throws SchedulerException {
        for (JobKey key : scheduler.getJobKeys(GroupMatcher.anyGroup())) {
            if (key.getName().equals(name)) {
                return key;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job não encontrado: " + name);
    }
}
