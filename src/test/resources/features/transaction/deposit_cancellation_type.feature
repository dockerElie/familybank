Feature: Deposit cancellation type
  Rule: possibility to easily identify the deposit's cancellation done by the account
  manager and the one done by the user
    Scenario: account manager cancelled a transaction
      Given account manager cancelled a transaction
      When user consults his deposits
      Then by default the deposits of the current year are displayed in descending order based on the Activate's date with the subsequent details
        | Name               | Date (Activate)      | Amount    | Status                   | Expiration date | Description
        | Raised for Malaria | 10/08/2024           | 1500 FCFA | cancelled (red text)     | 12/08/2024      | Raised fund for Malaria

    Scenario: user cancelled his deposit
      Given user cancelled a deposit
      When user consults his deposits
      Then by default the deposits of the current year are displayed in descending order based on the Activate's date with the subsequent details
        | Name               | Date (Activate)      | Amount    | Status                   | Expiration date | Description
        | Raised for Malaria | 10/08/2024           | 1500 FCFA | cancelled (yellow text) | 12/08/2024      | Raised fund for Malaria