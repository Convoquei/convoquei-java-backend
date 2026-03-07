package br.com.convoquei.backend.core.user.api.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
