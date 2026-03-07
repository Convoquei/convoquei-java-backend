package br.com.convoquei.backend.domain.user.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "O refresh token é obrigatório.")
        String refreshToken
) {
}

