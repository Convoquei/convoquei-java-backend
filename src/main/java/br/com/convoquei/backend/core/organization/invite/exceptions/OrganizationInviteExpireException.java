package br.com.convoquei.backend.core.organization.invite.exceptions;

import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;

import java.time.Instant;

public class OrganizationInviteExpireException extends DomainBusinessRuleException {
    public OrganizationInviteExpireException(Instant expireAt) {
        super("O convite expirou em %s".formatted(expireAt));
    }
}
