Feature: Transaction is created
  Scenario: user validated his deposit before the expiration date
    Given a deposit is done (status = done)
    When user validates his deposit
    And the deposit's expiration date is reached
    Then the application automatically creates the transaction with the following details
    | Name                     | Date       | Status  |
    | <deposit_name>           | 12/08/2024 | created |
    And the deposit's status changed to closed