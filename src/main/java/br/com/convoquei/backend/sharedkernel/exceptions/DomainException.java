package br.com.convoquei.backend.sharedkernel.exceptions;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
