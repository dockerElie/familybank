
Feature: user requests a deposit

  Rule: possibility to request a deposit

    Scenario: deposit's request status is valid
      Given an authenticated user
      When user initiates a request to reactivate a deposit
      And user provided the reason
      And deposit's status is on the valid list
        | status    |
        | done      |
        | validated |
        | closed    |
      Then an email is sent to the account manager for approving the request
        """
        Dear account manager,
        Some users requested you to approve their demand about reactivating some deposits.
        Please have a look.

        Do not reply to this email.
        """
      And the status of the deposit changes to requested
      And user can no longer interact with the deposit

    Scenario: While in the midst of requesting a deposit, user opts to reverse
    their most recent action
      Given user provided the reason for requesting a deposit
      When user chooses to undo his action
      Then the list of deposits is displayed based on the filter criteria