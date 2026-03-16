package br.com.convoquei.backend.core.user.services;

import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
import br.com.convoquei.backend.sharedkernel.services.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UserUploadImageProfileService {

    private final FileService fileService;
    private final CurrentUserProvider currentUserProvider;
    private final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png");
    private final long MAX_FILE_SIZE = 8 * 1024 * 1024; // 8MB em bytes

    public UserUploadImageProfileService(FileService fileService, CurrentUserProvider currentUserProvider) {
        this.fileService = fileService;
        this.currentUserProvider = currentUserProvider;
    }

    public String execute(MultipartFile file) throws IOException {
        if(file.isEmpty()) throw new IOException("O arquivo não pode estar vazio.");
        if(!ALLOWED_TYPES.contains(file.getContentType()))
            throw new RuntimeException("Apenas arquivos JPG e PNG são permitidos.");
        if(file.getSize() > MAX_FILE_SIZE) throw new IOException("O arquivo excede o limite de 8MB.");

        var currentUser = currentUserProvider.requireUser();
        var userId = currentUser.getId();
        var imageUrl = fileService.uploadImage(file, "user/" + userId + "/" + "profile.png");
        currentUser.setAvatarUrl("https://cdn.convoquei.com.br/" + imageUrl);
        return imageUrl;
    }
}
