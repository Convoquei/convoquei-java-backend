package br.com.convoquei.backend.domain.user.model.dto.response;

import br.com.convoquei.backend.domain.user.model.entity.User;

public record UserResponse(
        String name,
        String email,
        String avatarUrl,
        boolean isEmailVerified
) {
    public static UserResponse buildFromUser(User user) {
        return new UserResponse(
                user.getName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.isEmailVerified()
        );
    }
}
