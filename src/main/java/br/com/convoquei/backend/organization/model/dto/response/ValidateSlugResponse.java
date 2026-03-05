package br.com.convoquei.backend.organization.model.dto.response;

public record ValidateSlugResponse(
    String slug,
    boolean available
) { }

