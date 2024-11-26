Feature: Unauthenticated users

  Rule: Invalid credential is provided

    Scenario: Wrong credentials provided
      Given an authentication page is provided
      When user provides wrong credentials
      Then user should be redirected to the application login page
      And user should see the message
        """
        Wrong credentials. Please provide a valid password and username/email.
        """




