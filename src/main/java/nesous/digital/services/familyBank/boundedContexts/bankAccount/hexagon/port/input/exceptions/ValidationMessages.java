package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.exceptions;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationMessages {

    public static final String BALANCE_NEGATIVE = "Negative value. Please provide a positive value";
    public static final String ACCOUNT_HOLDER_LAST_NAME_EMPTY = "Account holder last name is empty";
    public static final String ACCOUNT_HOLDER_FIRST_NAME_EMPTY = "Account holder first name is empty";

    public static final String BALANCE_EMPTY = "Empty value. Please provide a positive numeric value";

    public static final String INVALID_ACCOUNT = "Invalid Account";

    public static final String INVALID_ACCOUNT_IDENTIFIER = "Invalid Account Identifier";

    public static final String PREVIOUS_DEPOSIT_STILL_ONGOING = "Previous deposit still ongoing";

    public static final String ACCOUNT_ID_EMPTY = "Account ID is empty";
    public static final String DEPOSIT_NAME_EMPTY = "deposit name is empty";

    public static final String DEPOSIT_ID_EMPTY = "Deposit identifier is empty";

    public static final String EXPIRATION_BEFORE_DEPOSIT_DATE = "Expiration date is before the deposit date";

    public static final String USER_EXPIRATION_BEFORE_EXPIRATION_DATE = "User Expiration date is before the expiration date";

    public static final String EXPIRATION_DATE_EMPTY = "Expiration date is empty";

    public static final String DEPOSIT_DATE_EMPTY = "Deposit date is empty";

    public static final String REASON_EMPTY = "Reason is empty";
}
