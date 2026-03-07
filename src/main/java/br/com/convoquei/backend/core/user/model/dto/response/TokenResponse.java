package br.com.convoquei.backend.core.user.model.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
