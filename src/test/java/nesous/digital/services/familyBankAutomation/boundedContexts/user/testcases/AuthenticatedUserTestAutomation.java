package nesous.digital.services.familyBankAutomation.boundedContexts.user.testcases;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Status;
import nesous.digital.services.familyBankAutomation.boundedContexts.user.logic.*;
import nesous.digital.services.familyBankAutomation.common.BaseClass;

public class AuthenticatedUserTestAutomation extends BaseClass {

    @Before
    public void setup(){
        // Setting of the Web driver
        BaseClass.setDriver();
        // login page with buttons : Login , Gmail , Facebook , Linkedin
        BaseClass.navigateUrl("/family/bank/user/login.do");

    }

    @Given("user selected an authentication form to connect")
    public void userSelectedAnAuthenticationFormToConnect() {
         // select login button
        LoginPage.login();
    }

    @When("user provides valid username & password")
    public void userProvidesValidUsernamePassword() {
        // login with Keycloak and displays Home Page
        HomePage.isLoading("knononel" , "test014");
    }

    @Then("user is activated")
    public void userIsActivated() {
        // verify that user is activated
        HomePage.isLoaded("home.do");
        HomePage.verifyUserStatus(Status.ACTIVATE);
    }

    @And("user is redirected to the home page of the application with the following details")
    public void userIsRedirectedToHomePageWithFollowingDetails() {
        HomePage.isLoaded("home.do");
        // TO DO
    }

    @Given("user selected facebook to connect")
    public void userSelectedFacebookToConnect() {
         // select social network App
         LoginPage.selectFacebookSocialNetwork();
    }

    @When("user provides valid credentials for facebook")
    public void userProvidesValidCredentialsForFacebook() {
         // encode valid credential for social network
        FacebookLoginPage.isLoadedLoginPage("test.com","u123");
    }

    @And("refuses the consent page for facebook")
    public void refusesTheConsentPageFacebook() {
         // refuse the consent
        FacebookLoginPage.RefuseConsentFacebook();
    }

    @Then("user is redirected to the authentication form of facebook")
    public void userIsRedirectedToTheAuthenticationFormOfFacebook() {
        FacebookLoginPage.isSocialNetworkFormPage();
    }



    @Given("user selected gmail to connect")
    public void userSelectedGmailToConnect() {
        // select social network App
        LoginPage.selectGmailSocialNetwork();
    }

    @When("user provides valid credentials for gmail")
    public void userProvidesValidCredentialsForGmail() {
        // encode valid credential for social network
        GmailLoginPage.isLoadedLoginPage("test.com","u156");
    }

    @And("refuses the consent page for gmail")
    public void refusesTheConsentPageGmail() {
        // refuse the consent
        GmailLoginPage.RefuseConsentGmail();
    }

    @Then("user is redirected to the authentication form of gmail")
    public void userIsRedirectedToTheAuthenticationFormOfGmail() {
        FacebookLoginPage.isSocialNetworkFormPage();
    }


    @Given("user selected linkedin to connect")
    public void userSelectedLinkedinToConnect() {
        // select social network App
        LoginPage.selectLinkedinSocialNetwork();
    }

    @When("user provides valid credentials for linkedin")
    public void userProvidesValidCredentialsForLinkedin() {
        // encode valid credential for Linkedin
        LinkedinLoginPage.isLoadedLoginPage("test.com","u986");
    }

    @And("refuses the consent page for linkedin")
    public void refusesTheConsentPageLinkedin() {
        // refuse the consent
        LinkedinLoginPage.RefuseConsentLinkedin();
    }

    @Then("user is redirected to the authentication form of linkedin")
    public void userIsRedirectedToTheAuthenticationFormOfLinkedin() {
        LinkedinLoginPage.isSocialNetworkFormPage();
    }

    @And("accepts the consent page for facebook")
    public void acceptsTheConsentPageFacebook() {
        // accept the consent
        FacebookLoginPage.acceptConsentFacebook();
    }

    @And("accepts the consent page for gmail")
    public void acceptsTheConsentPageGmail() {
        // accept the consent
        GmailLoginPage.acceptConsentGmail();
    }

    @And("accepts the consent page for linkedin")
    public void acceptsTheConsentPageLinkedin() {
        // accept the consent
        LinkedinLoginPage.acceptConsentLinkedin();
    }

    @Then("user is redirected to the home page of the application and activated with the following details")
    public void userIsRedirectedToTheHomePageOfTheApplicationAndActivatedWithTheFollowingDetails() {
        HomePage.isLoaded("home.do");
        HomePage.verifyUserStatus(Status.ACTIVATE);
    }

    @After
    public void teardown(){
        driver.quit();
    }

}
