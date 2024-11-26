
Feature: account manager activates a deposit

  Rule: bank account not created for any users
    Scenario: account manager cannot activate a deposit
      Given account manager is authenticated
      When account manager didn't configure any bank account
      Then account manager cannot activate a deposit.

  Rule: send an email to all users concerned by the activation of the deposit

    Scenario Outline: send an email when deposit is activated
      Given account manager is authenticated
      And created bank account for <users>
      When account manager activates a deposit <deposit_name> that expires on <expiration_date> for <users>
      Then an email is sent to <users> that already have a bank account using the following template
      """
        Dear <users>,
        A deposit has been activated and will expire on <expiration_date>.
        You can now make a deposit.

        Account manager.
        """
      Examples:
        | users    | deposit_name | expiration_date |
        | Elie     | deposit_1    | 20/12/2025      |
        | Jean     | deposit_2    | 31/12/2026      |

  Rule: deposit reminder is activated and will run until the deposit's expiration date

    Scenario Outline: deposit daily reminder is activated
      Given account manager created bank account for <users>
      When account manager activates the deposit <name> for <users>
      Then the deposit reminder is sent and will run until the deposit's expiration date <expiration_date>
        """
        Dear <users>,
        A kindly reminder to make a deposit before the expiration date <expiration_date>.

        Account manager.
        """
      And A first reminder will be sent 5 days after the activation date of the deposit
      And A other reminders will be sent every 5 days
      Examples:
        | users | name     | expiration_date |
        | elie  | deposit1 | 30-11-2024      |

  Rule: activate the deposit only for users that already have a bank account
    Scenario: account manager activates the deposit given the deposit name and the expiration date in the past
      Given account manager is authenticated
      When account manager provides the deposit name and the expiration date before the deposit creation date
      And saves
      Then a message appears, asking the user to provide the expiration's date in the future

    Scenario: account manager activates the deposit without giving the deposit name or the expiration date
      Given account manager is authenticated
      When account manager does not provide the deposit name and the expiration date
      And saves
      Then a message appears, asking the user to provide the missing deposit name and the missing expiration date

    Scenario: account manager activates a deposit
      Given account manager is authenticated
      When account manager activates a deposit for users that already have a bank account with the following details
        | Name                          | Expiration date    | Description              |
        | Raised fund for malaria       | 17/08/2023         | Raised funds for malaria |
      And There is no deposit already activated OR the last deposit expired
      Then the deposit status is set to activated and registered