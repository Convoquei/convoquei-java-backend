package br.com.convoquei.backend.core.user.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "O refresh token é obrigatório.")
        String refreshToken
) {
}

