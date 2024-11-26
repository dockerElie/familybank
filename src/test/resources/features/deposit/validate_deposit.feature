

Feature: user validates a deposit
  Rule: possibility to validate a deposit

    Scenario: user validates a deposit
      Given user made a deposit (status = done)
      And edited it
      When user validates a deposit
      Then the status of the deposit changes to validated
      And users remains on the modification page of the deposit.
      And the following message is presented to the user
      """
      Deposit has been validated successfully.
      """

    Scenario: no deposit has been validated until the expiration date of the deposit.
      Given user made a deposit
      And edited it
      When no deposit has been validated until the expiration date
      Then the following message is presented to the user
      """
      This deposit has already expired.
      """
      And the status changed to closed