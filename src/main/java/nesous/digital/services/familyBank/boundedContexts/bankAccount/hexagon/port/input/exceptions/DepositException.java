package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions;

public class DepositException extends RuntimeException {

    public DepositException(String message) {
        super(message);
    }
}
