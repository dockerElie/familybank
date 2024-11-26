Feature: account manager creates bank account

  Rule: when a bank account is created, send an email to the user

    Scenario Outline: send an email when bank account is created
      Given account manager selected user <users> for whom the bank account will be created to connect
      When account manager creates a bank account for user <users>
      Then an email is sent to <users> with the following template
        """
        Dear <users>,
        your bank account is now ready. Once the deposit will be activate, you can make money deposit.
        Regards

        Account manager
        """
      Examples:
        | users    |
        | Elie     |
        | Jean     |

  Rule: possibility to create deposit bank account for one or many users

    Scenario: option to choose the users
      Given account manager is authenticated
      When account manager selects at least one user
      Then account manager can create bank account

    Scenario: cannot create bank account
      Given account manager is authenticated
      When account manager does not select any user
      Then account manager cannot create bank account

    Scenario: creation of the deposit bank account succeed.
      Given account manager is authenticated
      When account manager creates a bank account for many users
      Then the following message is presented
      """
      Bank account created successfully.
      """
    Scenario: creation of the bank account failed.
      Given account manager is authenticated
      When account manager fails to create a bank account
      Then the following message is presented
      """
      Creation of the bank account failed.
      """