package br.com.convoquei.backend.app.api.admin.dto;

import java.time.Instant;

public record JobInfoResponse(
        String name,
        String group,
        String cron,
        String state,
        Instant previousFireTime,
        Instant nextFireTime
) {}
