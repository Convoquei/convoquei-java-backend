package br.com.convoquei.backend.domain.user.services;

import br.com.convoquei.backend.domain.user.model.dto.response.UserResponse;
import br.com.convoquei.backend.domain.user.provider.CurrentUserProvider;
import org.springframework.stereotype.Service;

@Service
public class GetUserProfileService {

    private final CurrentUserProvider currentUserProvider;

    public GetUserProfileService(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    public UserResponse execute() {
        var user = currentUserProvider.requireUser();
        return UserResponse.buildFromUser(user);
    }
}

