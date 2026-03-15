package br.com.convoquei.backend.core.user.services;

import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import br.com.convoquei.backend.sharedkernel.services.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserUploadImageProfileService {

    private final FileService fileService;
    private final CurrentUserProvider currentUserProvider;

    public UserUploadImageProfileService(FileService fileService, CurrentUserProvider currentUserProvider) {
        this.fileService = fileService;
        this.currentUserProvider = currentUserProvider;
    }

    public String execute(MultipartFile file) throws IOException {
        var currentUser = currentUserProvider.requireUser();
        var userId = currentUser.getId();
        var imageUrl = fileService.uploadImage(file, "user/" + userId + "/" + "profile.png");
        currentUser.setAvatarUrl("https://cdn.convoquei.com.br/" + imageUrl);
        return imageUrl;
    }
}
