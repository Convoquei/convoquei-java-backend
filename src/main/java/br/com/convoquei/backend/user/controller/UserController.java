package br.com.convoquei.backend.user.controller;

import br.com.convoquei.backend.user.model.dto.response.UserResponse;
import br.com.convoquei.backend.user.services.GetUserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/")
public class UserController {

    private final GetUserProfileService getUserProfileService;

    public UserController(GetUserProfileService getUserProfileService) {
        this.getUserProfileService = getUserProfileService;
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
}

