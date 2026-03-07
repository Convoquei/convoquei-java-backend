package br.com.convoquei.backend.core.organization.invite.controller;

import br.com.convoquei.backend.app.security.annotation.RequiredOrganizationPermission;
import br.com.convoquei.backend.core.organization.invite.model.dto.request.InviteMemberRequest;
import br.com.convoquei.backend.core.organization.invite.model.dto.response.OrganizationInviteDetailsResponse;
import br.com.convoquei.backend.core.organization.role.model.enums.OrganizationPermission;
import br.com.convoquei.backend.core.organization.invite.services.OrganizationInviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/invites")
public class OrganizationInviteController {
    private final OrganizationInviteService organizationInviteService;

    public OrganizationInviteController(OrganizationInviteService organizationInviteService) {
        this.organizationInviteService = organizationInviteService;
    }

    @PostMapping
    @Operation(summary = "Convidar membro para a organização")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Convite enviado com sucesso",
            content = @Content(schema = @Schema(implementation = OrganizationInviteDetailsResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.INVITE_MEMBERS)
    public OrganizationInviteDetailsResponse inviteMember(@PathVariable UUID organizationId, @RequestBody @Valid InviteMemberRequest request) {
        return organizationInviteService.invite(organizationId, request.email());
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Revogar convite de membro da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Convite revogado com sucesso")
    })
    @RequiredOrganizationPermission(OrganizationPermission.INVITE_MEMBERS)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void revokeInvite(@PathVariable UUID organizationId, @PathVariable String email) {
        organizationInviteService.revoke(organizationId, email);
    }
}
