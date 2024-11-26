Feature: user cancels a deposit
  Rule: possibility to cancel a deposit

    Scenario: user can cancel a deposit (status = done or status = validated)
      Given user edited a deposit
      When user cancels a deposit
      Then the status of deposit changes to cancelled
      And the amount is removed
      And the deposit user expiration date (if exists) is cancelled