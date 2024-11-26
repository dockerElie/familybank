Feature: after editing a deposit, user returns to his list of deposits
  Rule: possibility to return to the list of deposits
    Scenario: user edits the deposit and returns to the list of the deposit
      Given user selected a deposit
      When user chooses to return to his lists of deposits
      Then the list of his deposits is shown according to the filter criteria