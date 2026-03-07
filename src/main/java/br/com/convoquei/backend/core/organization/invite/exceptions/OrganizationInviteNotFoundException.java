package br.com.convoquei.backend.core.organization.invite.exceptions;

import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;

public class OrganizationInviteNotFoundException extends DomainNotFoundException {
    public OrganizationInviteNotFoundException() {
        super("O convite especificado não foi encontrado.");
    }
}
