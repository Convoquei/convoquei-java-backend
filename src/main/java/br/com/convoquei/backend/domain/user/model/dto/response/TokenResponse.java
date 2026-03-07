package br.com.convoquei.backend.domain.user.model.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
