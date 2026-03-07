package br.com.convoquei.backend.domain.organization.member.model.dto.request;

import br.com.convoquei.backend.app.api.dto.request.PagedRequest;
import br.com.convoquei.backend.domain.organization.member.model.enums.OrganizationMemberStatus;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class ListMembersByOrganizationRequest extends PagedRequest {

    @Size(min = 3, message = "name deve conter no mínimo 3 caracteres")
    private String name;

    private OrganizationMemberStatus status;

    private UUID roleId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public OrganizationMemberStatus getStatus() { return status; }
    public void setStatus(OrganizationMemberStatus status) { this.status = status; }

    public UUID getRoleId() { return roleId; }
    public void setRoleId(UUID roleId) { this.roleId = roleId; }
}
