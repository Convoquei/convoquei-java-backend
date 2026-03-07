package br.com.convoquei.backend.core.organization.group.api.dto.request;

import br.com.convoquei.backend.app.api.dto.request.PagedRequest;
import jakarta.validation.constraints.Size;

public class ListGroupsRequest extends PagedRequest {

    @Size(min = 2, message = "name deve conter no mínimo 2 caracteres")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

