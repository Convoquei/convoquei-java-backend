package br.com.convoquei.backend.core.organization.organization.model.dto.response;

public record ValidateSlugResponse(
    String slug,
    boolean available
) { }

