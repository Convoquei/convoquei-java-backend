package br.com.convoquei.backend.core.organization.function.api.controller;

import br.com.convoquei.backend.app.api.dto.request.PagedRequest;
import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.app.security.annotation.RequiredOrganizationPermission;
import br.com.convoquei.backend.core.organization.function.api.dto.request.AddFunctionMemberRequest;
import br.com.convoquei.backend.core.organization.function.api.dto.request.CreateFunctionRequest;
import br.com.convoquei.backend.core.organization.function.api.dto.request.ListFunctionsRequest;
import br.com.convoquei.backend.core.organization.function.api.dto.response.OrganizationFunctionMemberResponse;
import br.com.convoquei.backend.core.organization.function.api.dto.response.OrganizationFunctionResponse;
import br.com.convoquei.backend.core.organization.function.usecase.AddOrganizationFunctionMemberUseCase;
import br.com.convoquei.backend.core.organization.function.usecase.ClearOrganizationFunctionMembersUseCase;
import br.com.convoquei.backend.core.organization.function.usecase.CreateOrganizationFunctionUseCase;
import br.com.convoquei.backend.core.organization.function.usecase.DeleteOrganizationFunctionUseCase;
import br.com.convoquei.backend.core.organization.function.usecase.GetOrganizationFunctionMembersUseCase;
import br.com.convoquei.backend.core.organization.function.usecase.ListOrganizationFunctionsUseCase;
import br.com.convoquei.backend.core.organization.function.usecase.RemoveOrganizationFunctionMemberUseCase;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;
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
@RequestMapping("/api/v1/organizations/{organizationId}/functions")
public class OrganizationFunctionController {

    private final CreateOrganizationFunctionUseCase createOrganizationFunctionUseCase;
    private final ListOrganizationFunctionsUseCase listOrganizationFunctionsUseCase;
    private final GetOrganizationFunctionMembersUseCase getOrganizationFunctionMembersUseCase;
    private final AddOrganizationFunctionMemberUseCase addOrganizationFunctionMemberUseCase;
    private final RemoveOrganizationFunctionMemberUseCase removeOrganizationFunctionMemberUseCase;
    private final ClearOrganizationFunctionMembersUseCase clearOrganizationFunctionMembersUseCase;
    private final DeleteOrganizationFunctionUseCase deleteOrganizationFunctionUseCase;

    public OrganizationFunctionController(
            CreateOrganizationFunctionUseCase createOrganizationFunctionUseCase,
            ListOrganizationFunctionsUseCase listOrganizationFunctionsUseCase,
            GetOrganizationFunctionMembersUseCase getOrganizationFunctionMembersUseCase,
            AddOrganizationFunctionMemberUseCase addOrganizationFunctionMemberUseCase,
            RemoveOrganizationFunctionMemberUseCase removeOrganizationFunctionMemberUseCase,
            ClearOrganizationFunctionMembersUseCase clearOrganizationFunctionMembersUseCase,
            DeleteOrganizationFunctionUseCase deleteOrganizationFunctionUseCase
    ) {
        this.createOrganizationFunctionUseCase = createOrganizationFunctionUseCase;
        this.listOrganizationFunctionsUseCase = listOrganizationFunctionsUseCase;
        this.getOrganizationFunctionMembersUseCase = getOrganizationFunctionMembersUseCase;
        this.addOrganizationFunctionMemberUseCase = addOrganizationFunctionMemberUseCase;
        this.removeOrganizationFunctionMemberUseCase = removeOrganizationFunctionMemberUseCase;
        this.clearOrganizationFunctionMembersUseCase = clearOrganizationFunctionMembersUseCase;
        this.deleteOrganizationFunctionUseCase = deleteOrganizationFunctionUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar funções da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PagedResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.MEMBER)
    public PagedResponse<OrganizationFunctionResponse> listFunctions(
            @PathVariable UUID organizationId,
            @Valid @ModelAttribute ListFunctionsRequest request
    ) {
        return listOrganizationFunctionsUseCase.execute(organizationId, request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar função na organização")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Função criada com sucesso",
            content = @Content(schema = @Schema(implementation = OrganizationFunctionResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_FUNCTIONS)
    public OrganizationFunctionResponse createFunction(
            @PathVariable UUID organizationId,
            @RequestBody @Valid CreateFunctionRequest request
    ) {
        return createOrganizationFunctionUseCase.execute(organizationId, request);
    }

    @DeleteMapping("/{functionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletar função da organização")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Função deletada com sucesso")
    })
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_FUNCTIONS)
    public void deleteFunction(
            @PathVariable UUID organizationId,
            @PathVariable UUID functionId
    ) {
        deleteOrganizationFunctionUseCase.execute(organizationId, functionId);
    }

    @GetMapping("/{functionId}/members")
    @Operation(summary = "Listar membros da função")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PagedResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.MEMBER)
    public PagedResponse<OrganizationFunctionMemberResponse> getFunctionMembers(
            @PathVariable UUID organizationId,
            @PathVariable UUID functionId,
            @Valid @ModelAttribute PagedRequest request
    ) {
        return getOrganizationFunctionMembersUseCase.execute(organizationId, functionId, request);
    }

    @PostMapping("/{functionId}/members")
    @Operation(summary = "Adicionar membro à função")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Membro adicionado com sucesso",
            content = @Content(schema = @Schema(implementation = OrganizationFunctionResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_FUNCTIONS)
    public OrganizationFunctionResponse addMember(
            @PathVariable UUID organizationId,
            @PathVariable UUID functionId,
            @RequestBody @Valid AddFunctionMemberRequest request
    ) {
        return addOrganizationFunctionMemberUseCase.execute(organizationId, functionId, request);
    }

    @DeleteMapping("/{functionId}/members/{id}")
    @Operation(summary = "Remover membro da função")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Membro removido com sucesso",
            content = @Content(schema = @Schema(implementation = OrganizationFunctionResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_FUNCTIONS)
    public OrganizationFunctionResponse removeMember(
            @PathVariable UUID organizationId,
            @PathVariable UUID functionId,
            @PathVariable UUID id
    ) {
        return removeOrganizationFunctionMemberUseCase.execute(organizationId, functionId, id);
    }

    @DeleteMapping("/{functionId}/members")
    @Operation(summary = "Remover todos os membros da função")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Membros removidos com sucesso",
            content = @Content(schema = @Schema(implementation = OrganizationFunctionResponse.class)))
    })
    @RequiredOrganizationPermission(OrganizationPermission.MANAGE_FUNCTIONS)
    public OrganizationFunctionResponse clearFunctionMembers(
            @PathVariable UUID organizationId,
            @PathVariable UUID functionId
    ) {
        return clearOrganizationFunctionMembersUseCase.execute(organizationId, functionId);
    }
}
