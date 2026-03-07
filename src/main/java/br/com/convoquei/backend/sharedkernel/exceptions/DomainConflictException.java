package br.com.convoquei.backend.sharedkernel.exceptions;

public class DomainConflictException extends DomainException {
    public DomainConflictException(String message) {
        super(message);
    }
}
