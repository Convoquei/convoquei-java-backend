package br.com.convoquei.backend.core.user.api.dto.response;

import br.com.convoquei.backend.core.user.entity.User;

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
