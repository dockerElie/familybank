Feature: Authenticated users

  Rule: Valid credentials is provided

    Scenario: User selects facebook - Consent page refuse
      Given user selected facebook to connect
      When user provides valid credentials for facebook
      And refuses the consent page for facebook
      Then user is redirected to the authentication form of facebook

    Scenario: User selects gmail - Consent page refuse
      Given user selected gmail to connect
      When user provides valid credentials for gmail
      And refuses the consent page for gmail
      Then user is redirected to the authentication form of gmail

    Scenario: User selects linkedin - Consent page refuse
      Given user selected linkedin to connect
      When user provides valid credentials for linkedin
      And refuses the consent page for linkedin
      Then user is redirected to the authentication form of linkedin

    Scenario: User selects facebook - Consent page accept
      Given user selected facebook to connect
      When user provides valid credentials for facebook
      And accepts the consent page for facebook
      Then user is redirected to the home page of the application and activated with the following details
        | Identifier | Last Name             | First Name  | User Name | Email          | Activated | Created At
        | iosd-sdp   | NONO NOUAGONG         | Elie Michel | knononel  | elie@gmail.com | ACTIVATE  | 12-01-2024

    Scenario: User selects gmail - Consent page accept
      Given user selected gmail to connect
      When user provides valid credentials for gmail
      And accepts the consent page for gmail
      Then user is redirected to the home page of the application and activated with the following details
        | Identifier | Last Name             | First Name  | User Name | Email          | Activated | Created At
        | iosd-sdp   | NONO NOUAGONG         | Elie Michel | knononel  | elie@gmail.com | ACTIVATE  | 12-01-2024

    Scenario: User selects linkedin - Consent page accept
      Given user selected linkedin to connect
      When user provides valid credentials for linkedin
      And accepts the consent page for linkedin
      Then user is redirected to the home page of the application and activated with the following details
        | Identifier | Last Name             | First Name  | User Name | Email          | Activated | Created At
        | iosd-sdp   | NONO NOUAGONG         | Elie Michel | knononel  | elie@gmail.com | ACTIVATE  | 12-01-2024

    Scenario: User selects the authentication form
      Given user selected an authentication form to connect
      When user provides valid username & password
      Then user is activated
      And user is redirected to the home page of the application with the following details
        | Identifier | User Name | Status
        | iosd-sdp   | knononel  | ACTIVATE