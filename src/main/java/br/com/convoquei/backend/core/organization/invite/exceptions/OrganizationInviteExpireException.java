package br.com.convoquei.backend.core.organization.invite.exceptions;

import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;

import java.time.LocalDateTime;

public class OrganizationInviteExpireException extends DomainBusinessRuleException {
    public OrganizationInviteExpireException(LocalDateTime expireAt) {
        super("O convite expirou em %s".formatted(expireAt));
    }
}
