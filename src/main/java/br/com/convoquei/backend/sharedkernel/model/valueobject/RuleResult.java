package br.com.convoquei.backend.sharedkernel.model.valueobject;

public record RuleResult(boolean success, String message) {

    public static RuleResult ok() {
        return new RuleResult(true, null);
    }

    public static RuleResult failure(String message) {
        return new RuleResult(false, message);
    }
}