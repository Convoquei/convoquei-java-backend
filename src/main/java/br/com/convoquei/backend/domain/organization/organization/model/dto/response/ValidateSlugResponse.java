package br.com.convoquei.backend.domain.organization.organization.model.dto.response;

public record ValidateSlugResponse(
    String slug,
    boolean available
) { }

