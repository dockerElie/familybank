Feature: Manage transaction
  Rule: possibility to cancel a transaction

    Scenario: total amount of user's bank account not updated
      Given account manager selected one transaction from the list (status = created)
      When account manager provides the reason of the cancellation
      And saves
      Then the amount of the user's bank account is not updated
      And the status of the transaction change to cancelled
      And the status of the deposit changes to cancelled
      And the system records the date when the transaction is cancelled

    Scenario: not provide a reason
      Given account manager selected one transaction from the list (status = created)
      When account manager cancels the transaction
      And does not provide the reason
      Then the system asks to account manager to provide the missing reason

    Scenario: provide a reason
      Given account manager selected one transaction from the list (status = created)
      When account manager cancels the transaction
      And provides the reason
      Then account manager can save

    Scenario: send an email
      Given account manager selected one transaction from the list (status = created)
      When account manager cancels the transaction
      Then an email is sent out to the user with the following format
      """
      Dear <username>,
      your transaction has been cancelled for the following reason
      <reason>. Therefore the amount of your bank account has not been updated.

      Please do not reply to this email.
      """
  Rule: possibility to validate a transaction
    Scenario: total amount of the user's bank account is updated
      Given account manager selected one transaction from the list (status = created)
      When account manager validates the transaction
      Then the status of the transaction changes to "Validated"
      And user can see the updated amount of his/her bank account