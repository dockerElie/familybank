package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
