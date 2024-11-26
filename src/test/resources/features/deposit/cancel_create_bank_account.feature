Feature: manager cancels the creation of the bank account user

  Scenario: manager can cancel the creation of the bank account
    Given manager is about creating a bank account
    When user cancels the creationo
    Then the system clears the selection of the users