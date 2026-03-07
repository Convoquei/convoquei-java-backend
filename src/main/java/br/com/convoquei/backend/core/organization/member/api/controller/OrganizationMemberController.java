package br.com.convoquei.backend.core.organization.member.api.controller;

import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.app.security.annotation.RequiredOrganizationPermission;
import br.com.convoquei.backend.core.organization.member.api.dto.request.ListMembersByOrganizationRequest;
import br.com.convoquei.backend.core.organization.member.api.dto.request.UpdateMemberGroupsRequest;
import br.com.convoquei.backend.core.organization.member.api.dto.response.MyMemberhipResponse;
import br.com.convoquei.backend.core.organization.member.api.dto.response.OrganizationMemberResponse;
import br.com.convoquei.backend.core.organization.member.services.OrganizationMemberService;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/members")
public class OrganizationMemberController {

    private final OrganizationMemberService organizationMemberService;

    public OrganizationMemberController(OrganizationMemberService organizationMemberService) {
        this.organizationMemberService = organizationMemberService;
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados de membro da organização do usuário autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso",
            content = @Content(schema = @Schema(implementation = MyMemberhipResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.MEMBER)
    public MyMemberhipResponse getMembership(@PathVariable UUID organizationId) {
        return organizationMemberService.getMembership(organizationId);
    }

    @GetMapping
    @Operation(summary = "Listar membros da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PagedResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.MEMBER)
    public PagedResponse<OrganizationMemberResponse> listMembers(@PathVariable UUID organizationId, @Valid @ModelAttribute ListMembersByOrganizationRequest request) {
        return organizationMemberService.listMembers(organizationId, request);
    }

    @DeleteMapping("/{memberId}")
    @Operation(summary = "Remover membro da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Membro removido com sucesso",
            content = @Content(schema = @Schema(implementation = OrganizationMemberResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.KICK_MEMBERS)
    public OrganizationMemberResponse kickMemberOfOrganization(@PathVariable UUID organizationId, @PathVariable UUID memberId) {
        return organizationMemberService.kickMemberOfOrganization(organizationId, memberId);
    }

    @PutMapping("/{memberId}/groups")
    @Operation(summary = "Atualizar grupos do membro")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Grupos atualizados com sucesso",
            content = @Content(schema = @Schema(implementation = OrganizationMemberResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_GROUPS)
    public OrganizationMemberResponse updateMemberGroups(
            @PathVariable UUID organizationId,
            @PathVariable UUID memberId,
            @RequestBody @Valid UpdateMemberGroupsRequest request
    ) {
        return organizationMemberService.updateMemberGroups(organizationId, memberId, request);
    }

}
