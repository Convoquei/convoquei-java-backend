package br.com.convoquei.backend.core.organization.organization.api.dto.response;

public record ValidateSlugResponse(
    String slug,
    boolean available
) { }

