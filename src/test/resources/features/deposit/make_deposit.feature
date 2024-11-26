
Feature: user makes a deposit

  Rule: make a deposit if the status is activated

    Scenario: deposit reminder is not sent out
      Given user selected an activated deposit
      When user makes a deposit before the deposit expiration date set by the account manager
      And deposit amount is not empty
      Then the daily deposit reminder should cease

    Scenario: no amount is provided
      Given user selected an activated deposit
      When user attemps to make a deposit with an empty value
      Then the saved deposit amount is zero

    Scenario: deposit amount is a negative value
      Given user selected an activated deposit
      When  user attemps to make a deposit with a negative value
      Then the following message is presented to the user
      """
      Negative value. Please provide a positive value.
      """

    Scenario: deposit amount isn't a numerical value
      Given user selected an activated deposit
      When user attemps to make a deposit with a non-numeric value
      Then the following message is presented to the user
      """
      Wrong value. Please provide a numeric value.
      """

    Scenario: While in the midst of initiating a deposit, user opts to reverse
                                                                        their most recent action
      Given user provided the deposit amount
      When user chooses to undo his action
      Then the system clears the amount
      And user remains on the modification page of the deposit

    Scenario: returns to the list of deposits
      Given user made a deposit
      When user returns to the list of his deposits
      Then the system redirects the user to his list of deposits according to the filter criteria

    Scenario: makes a deposit
      Given user selected an activated deposit
      When user saves the deposit amount
      Then the status of the deposit should change from activated to done
      And users is redirected to the list of deposit.

    Scenario: no deposit has been done until the expiration date of the deposit.
      Given user selected an activated deposit
      When user saves the deposit amount
      Then the following message is presented to the user
      """
      This deposit already expired.
      """
      And the status changes to closed
      And systems redirects the user to its list of deposits according to the filter criteria