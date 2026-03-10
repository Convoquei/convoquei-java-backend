package br.com.convoquei.backend.core.organization.event.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record RespondEventAvailabilityRequest(
        @NotNull(message = "O campo 'available' é obrigatório.")
        Boolean available
) {}

