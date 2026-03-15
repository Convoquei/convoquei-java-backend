package br.com.convoquei.backend.core.user.api.controller;

import br.com.convoquei.backend.core.user.api.dto.response.UserResponse;
import br.com.convoquei.backend.core.user.services.GetUserProfileService;
import br.com.convoquei.backend.core.user.services.UserUploadImageProfileService;
import br.com.convoquei.backend.sharedkernel.services.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users/")
public class UserController {

    private final GetUserProfileService getUserProfileService;
    private final UserUploadImageProfileService userUploadImageProfileService;

    public UserController(GetUserProfileService getUserProfileService, FileService fileService, UserUploadImageProfileService userUploadImageProfileService) {
        this.getUserProfileService = getUserProfileService;
        this.userUploadImageProfileService = userUploadImageProfileService;
    }

    @GetMapping("me/profile")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obter perfil do usuário logado", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso",
            content = @Content(schema = @Schema(implementation = UserResponse.class)))
    })
    public UserResponse getMyProfile() {
        return getUserProfileService.execute();
    }

    @PostMapping("me/image-profile")
    public String setImageProfile(@RequestParam("file") MultipartFile file) throws IOException {
        return userUploadImageProfileService.execute(file);
    }
}

