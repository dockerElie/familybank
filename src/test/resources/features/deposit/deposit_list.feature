
Feature: User can see the list of deposits

  Rule: Automatically update the list of deposits
  when user filters by status, specific range dates or deposit identifier

    Scenario: user searches for deposits containing specific identifier
      Given user is consulted his deposits
        | Identifier  | Name               | Date (Activate)        | Amount    | Status               | User expiration date | Expiration date | Description
        | 01-DEP-2024 | Raised for Malaria | 10/08/2024             | 1500 FCFA | done                 | 10/08/2025           | 12/08/2024      | Raised fund for Malaria

        | Identifier  | Name               | Date (Activate)        | Amount    | Status               | User expiration date | Expiration date | Description
        | 02-DEP-2022 | Raised for AIDS    | 12/08/2022             | N/A       | activated            |                      | 18/08/2022      | Raised fund for AIDS

      When user searches for deposits that the identifier contains the criteria '22'
      Then The updated list is presented in a descending order based on deposit's date

        | Identifier  | Name               | Date (Activate)        | Amount    | Status               | User expiration date | Expiration date | Description
        | 02-DEP-2022 | Raised for AIDS    | 12/08/2022             | N/A       | activated            |                      | 18/08/2022      | Raised fund for AIDS

    Scenario: the user searches for deposit with a specific status
      Given user is consulted his deposits
      When user searches for deposits with specific status
      Then the following status's options are displayed
        | Status      |
        | activated   |
        | done        |
        | requested   |
        | reactivated |
        | cancelled   |
        | validated   |
        | denied      |
        | closed      |
    Scenario: the user specifically searches for deposits between 25/08/2022 - 09/08/2023
      Given user is consulted his deposits
        | Identifier  | Name               | Date (Activate)        | Amount    | Status    | User expiration date | Expiration date | Description
        | 01-DEP-2024 | Raised for Malaria | 10/08/2024             | 1500 FCFA | validated | 10/08/2025           | 12/08/2024      | Raised fund for Malaria

        | Identifier  | Name               | Date (Activate)        | Amount    | Status       | User expiration date | Expiration date | Description
        | 02-DEP-2022 | Raised for AIDS    | 12/08/2022             | N/A       | activated    |                      | 18/08/2022      | Raised fund for AIDS
      When user searches for deposits between 25/08/2022 - 09/08/2023
      Then The updated list is presented in a descending order based on deposit's date
        | Identifier  | Name               | Date (Activate)        | Amount    | Status    | User expiration date | Expiration date | Description
        | 02-DEP-2022 | Raised for AIDS    | 25/08/2022             | N/A       | activated |                      | 18/08/2022      | Raised fund for AIDS

    Scenario: the user searches for deposits with a specific status"
      Given user is consulted his deposits
        | Identifier  | Name               | Date (Activate)      | Amount    | Status    | User expiration date | Expiration date | Description
        | 01-DEP-2024 | Raised for Malaria | 10/08/2024           | 1500 FCFA | validated | 10/08/2025           | 12/08/2024      | Raised fund for Malaria

        | Identifier  | Name               | Date (Activate)      | Amount    | Status    | User expiration date | Expiration date | Description
        | 02-DEP-2022 | Raised for AIDS    | 10/08/2022           | N/A       | activated |                      | 12/08/2022      | Raised fund for AIDS

        | Identifier  | Name               | Date (Activate)      | Amount    | Status    | User expiration date | Expiration date | Description
        | 03-DEP-2022 | Raised for AIDS    | 09/06/2022           | 1500 FCFA | cancelled |                      | 12/08/2022      | Raised fund for AIDS
      When user searches for deposits with a status of "validated" or "activated."
      Then The updated list is presented in a descending order based on deposit's date

        | Identifier  | Name               | Date (Activate)       | Amount    | Status    | User expiration date | Expiration date | Description
        | 01-DEP-2024 | Raised for Malaria | 10/08/2024            | 1500 FCFA | validated | 10/08/2025           | 12/08/2024      | Raised fund for Malaria

        | Identifier  | Name               | Date (Activate)       | Amount    | Status    | User expiration date | Expiration date | Description
        | 02-DEP-2022 | Raised for AIDS    | 10/08/2022            | N/A       | activated |                      | 12/08/2022      | Raised fund for AIDS

  Rule: Display the message 'No available deposit for this year' if there is no deposit for the current year

    Scenario: No available deposits for current year
      Given an authenticated user
      When user consults his deposits
      Then a blank page shoud appear with the message
      """
        No deposit available for this year.
      """
  Rule: Display the message 'No available data' if there is no deposit

    Scenario: No available deposits
      Given an authenticated user
      When user consults his deposits
      Then a blank page should appear with the message
      """
        No deposit available.
      """
  Rule: Display list of deposits sorted in descending order
  based on deposit's Activate date

    Scenario: the user has set up the expiration date
      Given an authenticated user
      When user consults the list of deposits
      Then by default the deposits (sort in descending order based on the deposit date) of the current year are displayed with the subsequent details
        | Identifier  | Name               | Date (Activate)      | Amount    | Status    | User expiration date | Expiration date | Description
        | 01-DEP-2024 | Raised for Malaria | 10/08/2024           | 1500 FCFA | done      | 10/08/2025           | 12/08/2024      | Raised fund for Malaria

    Scenario: the user did not set up the expiration date
      Given an authenticated user
      When user consults his deposits
      Then by default the deposits (sort in descending order based on the deposit date) of the current year are displayed with the subsequent details
        | Identifier  | Name                     | Date (Activate)      | Amount    | Status             | Expiration date | Description
        | 01-DEP-2024 | Raised funds for malaria | 10/08/2024           | N/A       | activated          | 12/08/2024      | Raised fund for malaria

    Scenario: deposit was denied
      Given an authenticated user
      When user consults the list of deposits
      Then by default the deposits (sort in descending order based on the deposit date) of the current year are displayed with the subsequent details
        | Identifier  | Name                     | Date (Activate)            | Amount     | Status    | Expiration date         | Description             | Reason(denied)
        | 01-DEP-2024 | Raised funds for malaria | 10/08/2024                 | 1500 FCFA  | denied    | 12/08/2024              | Raised funds for malaria| Invalid

    Scenario: user reviews the information about the available deposits
      Given an authenticated user
      When user consults his deposits
      Then the deposits (sort in descending order based on the deposit date) of the current year are displayed by default, with the subsequent details
        | Identifier            | Name                    | Date (Activate)      | Amount    | Status    | Expiration date | Description
        | 01-DEP-2024           | Raised funds for malaria| 10/08/2024           | N/A       | activated | 12/08/2024      | Raised funds for malaria
