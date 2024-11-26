Feature: account manager validate or reject a request

  Rule: display people who have requested for reactivating a deposit
    Scenario: account manager consults list of users who made a request for reactivating a deposit
      Given account manager is authenticated
      When account manager consults the list of users who made a request for reactivating a deposit
      Then the list of users is presented in descending order based on the request's date with following details
        | Person       | Date of the request    | Reason for reactivating the deposit |
        | Elie         | 15/08/2023             | Made a mistake                      |

  Rule: possibility to validate a deposit's request
    Scenario: validate a deposit's request
      Given user made a request for reactivating a deposit
      When account manager validates the request
      Then the deposit amount is removed
      And the deposit user expiration date is cancelled
      And the deposit expiration date is updated
      And the deposit's status changes to reactivated

  Rule: possibility to reject a deposit's request
    Scenario: reject a deposit's request
      Given user made a request for reactivating the deposit
      When account manager rejects the request
      Then deposit status changes to denied

  Rule: do not send an email if request is denied
    Scenario: deposit's request is denied
      Given user made a request for reactivating a deposit
      When account manager rejects the request
      Then No email is sent to the user

  #In this scenario '<deposit_id>', '<email>' and '<expiration_date>' are placeholders for parameters
  #In your step definitions, you will need to use regular expressions to capture these parameters and pass them to
  #email sending logic
  Rule: send an email if request is validated
    Scenario: deposit's request is validated
      Given user made a request for reactivating a deposit "<deposit_name>"
      When account manager validates the reactivation of the deposit request that expires on "<expiration_date>"
      Then an email is sent out to the user "<user>" with the following template
          """
          Dear <user> your deposit <deposit_name> has been reactivated.
          You can now make a deposit.
          The deposit will expire on <expiration_date>.

          Account manager.
          """