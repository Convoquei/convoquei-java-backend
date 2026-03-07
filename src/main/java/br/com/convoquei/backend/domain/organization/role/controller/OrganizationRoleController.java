package br.com.convoquei.backend.domain.organization.role.controller;

import br.com.convoquei.backend.app.api.dto.request.PagedRequest;
import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.app.security.annotation.RequiredOrganizationPermission;
import br.com.convoquei.backend.domain.organization.role.model.dto.request.CreateOrganizationRoleRequest;
import br.com.convoquei.backend.domain.organization.role.model.dto.request.UpdateOrganizationRolePermissionsRequest;
import br.com.convoquei.backend.domain.organization.role.model.dto.response.OrganizationPermissionResponse;
import br.com.convoquei.backend.domain.organization.role.model.dto.response.OrganizationRoleResponse;
import br.com.convoquei.backend.domain.organization.role.model.dto.response.OrganizationRoleMemberResponse;
import br.com.convoquei.backend.domain.organization.role.model.enums.OrganizationPermission;
import br.com.convoquei.backend.domain.organization.role.services.OrganizationRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/roles")
public class OrganizationRoleController {

    private final OrganizationRoleService organizationRoleService;

    public OrganizationRoleController(OrganizationRoleService organizationRoleService) {
        this.organizationRoleService = organizationRoleService;
    }

    @GetMapping("/permissions")
    @Operation(summary = "Listar permissões disponíveis da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = OrganizationPermissionResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.OWNER)
    public List<OrganizationPermissionResponse> listAvailablePermissions(@PathVariable UUID organizationId) {
        return organizationRoleService.listAvailablePermissions();
    }

    @GetMapping
    @Operation(summary = "Listar cargos da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PagedResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.OWNER)
    public PagedResponse<OrganizationRoleResponse> listRoles(
            @PathVariable UUID organizationId,
            @Valid @ModelAttribute PagedRequest request) {
        return organizationRoleService.listRoles(organizationId, request);
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "Obter detalhes de um cargo da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso",
            content = @Content(schema = @Schema(implementation = OrganizationRoleResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.OWNER)
    public OrganizationRoleResponse getRole(
            @PathVariable UUID organizationId,
            @PathVariable UUID roleId) {
        return organizationRoleService.getRole(organizationId, roleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar cargo na organização")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cargo criado com sucesso",
            content = @Content(schema = @Schema(implementation = OrganizationRoleResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.OWNER)
    public OrganizationRoleResponse createRole(
            @PathVariable UUID organizationId,
            @RequestBody @Valid CreateOrganizationRoleRequest request) {
        return organizationRoleService.createRole(organizationId, request);
    }

    @PutMapping("/{roleId}/permissions")
    @Operation(summary = "Atualizar permissões de um cargo da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permissões atualizadas com sucesso",
            content = @Content(schema = @Schema(implementation = OrganizationRoleResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.OWNER)
    public OrganizationRoleResponse updateRolePermissions(
            @PathVariable UUID organizationId,
            @PathVariable UUID roleId,
            @RequestBody @Valid UpdateOrganizationRolePermissionsRequest request) {
        return organizationRoleService.updateRolePermissions(organizationId, roleId, request);
    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir cargo da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cargo excluído com sucesso")
    })
    @RequiredOrganizationPermission(OrganizationPermission.OWNER)
    public void deleteRole(
            @PathVariable UUID organizationId,
            @PathVariable UUID roleId) {
        organizationRoleService.deleteRole(organizationId, roleId);
    }

    @PostMapping("/{roleId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Atribuir cargo a um membro da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cargo atribuído com sucesso")
    })
    @RequiredOrganizationPermission(OrganizationPermission.OWNER)
    public void assignRole(
            @PathVariable UUID organizationId,
            @PathVariable UUID roleId,
            @PathVariable UUID memberId) {

        organizationRoleService.assignRole(organizationId, roleId, memberId);
    }

    @DeleteMapping("/{roleId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover cargo de um membro da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cargo removido com sucesso")
    })
    @RequiredOrganizationPermission(OrganizationPermission.OWNER)
    public void unassignRole(
            @PathVariable UUID organizationId,
            @PathVariable UUID roleId,
            @PathVariable UUID memberId) {
        organizationRoleService.unassignRole(organizationId, roleId, memberId);
    }

    @GetMapping("/{roleId}/members")
    @Operation(summary = "Listar membros atribuídos a uma role da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PagedResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.MEMBER)
    public PagedResponse<OrganizationRoleMemberResponse> listRoleMembers(
            @PathVariable UUID organizationId,
            @PathVariable UUID roleId,
            @Valid @ModelAttribute PagedRequest request) {

        return organizationRoleService.listRoleMembers(organizationId, roleId, request);
    }

}
