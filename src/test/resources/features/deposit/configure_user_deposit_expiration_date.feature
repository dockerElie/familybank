Feature: user sets the expiration date of the deposit

  Rule: set the expiration date is not possible if the deposit status is activated
    Scenario: set expiration date not possible
      Given user is successfully connected
      When user edits an activated deposit
      Then user cannot configure the expiration date

  Rule: deposit's reminder should be scheduled once the maximum expiration date, as determined
  by the user, is reached

    Scenario: No reminder sent out
      Given user made a deposit (status = done)
      When user sets the expiration date
      Then the next daily reminder will be scheduled once the maximum expiration date for all of the user's deposits has been reached.

  Rule: deposit's expiration date set by the user should be equal or greater than
  the deposit expiration date set by the account manager

    Scenario: user sets the deposit expiration date
      Given user made a deposit (status = done)
      And edited it
      When user sets the expiration date
      Then the calendar is presented to the user with the dates beyond the expiration date set by the account manager
      And user selects the date
      And user saves
      And user remains on the modification page of the deposit

    Scenario: while in the midst of setting the deposit's expiration date, users opts to undo his action
      Given the calendar is presented to the user for setting the expiration date
      When user cancels his action
      Then the calendar disappears
      And user remains to the modification page of the deposit

