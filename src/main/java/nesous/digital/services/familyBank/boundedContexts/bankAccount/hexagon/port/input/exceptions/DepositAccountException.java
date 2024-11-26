package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions;

public class DepositAccountException extends RuntimeException {

    public DepositAccountException(String message) {
        super(message);
    }
}
