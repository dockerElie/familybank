Feature: Access deposit's history

  Rule: display the deposit's history
    Scenario: account manager consults deposit's history.
      Given account manager consulted the list of deposits that have been reactivated
      When account manager selects one deposit to check the history
      Then the subsequent details are presented in descending order based on the date when the deposit's status has changed
        | Identifier  | Activate date | Person | Deposit request's status | Date (when deposit has been cancelled)
        | 01-DEP_2023 | 12/08/2023    | Elie   | cancelled                | 20/08/2023

        | Identifier  | Activate date | Person | Deposit request's status | Date (when deposit has been validated)
        | 01-DEP_2023 | 12/08/2023    | Elie   | validated                | 16/07/2023

  Rule: display list of deposits that have been reactivated
    Scenario: no deposits have been reactivated
      Given account manager is authenticated
      And no deposit has been reactivated
      When account manager consults the list of deposits that have been reactivated
      Then account manager sees the message
        """
        No data available
        """

    Scenario: show all deposits that have been reactivated
      Given account manager is authenticated
      When account manager consults the list of deposits that have been reactivated
      Then the list of deposits is presented in descending order based on the Activate's date
        | Identifier    | Activate date | Request date     | Reactivate date | Deposit's name                | Description              | status
        | 01-DEP_2023   | 12/08/2023    | 15/08/2023       | 20/08/2023      | Raised funds for malaria      | Raised funds for malaria | reactivated

        | Identifier    |  Activate date | Request date     | Reactivate date | Deposit's name                | Description              | status
        | 01-DEP_2021   | 16/08/2021    | 21/09/2021       | 03/10/2021       | Raised funds for malaria      | Raised funds for malaria | reactivated