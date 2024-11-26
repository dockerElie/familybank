package nesous.digital.services.familyBankAutomation.boundedContexts.user.logic;


import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.Person;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Status;
import nesous.digital.services.familyBankAutomation.boundedContexts.user.data.object.SocialNetwork;
import nesous.digital.services.familyBankAutomation.common.BaseClass;
import org.openqa.selenium.By;

public class HomePage {

    protected static String txtUserName = "username";
    protected static String txtPassword = "password";
    protected static String txtSignIn = "kc-login";
    protected static String txtStatusUser = "";
    protected static String txtIdentifier = "";
    protected static String txtLastName = "";
    protected static String txtFirstName = "";
    protected static String txtEmail = "";
    protected static String txtCreatedAt = "";
    protected static String txtAccept = "accept";
    protected static String txtRefuse = "refuse";

    public static void isLoading(String txtUserName , String txtPassword) {
        encodeUserName(txtUserName);
        encodePassword(txtPassword);
        clickSignIn();
    }

    public static void isLoadingSocialNetwork(String socialNetwork) {
        if (socialNetwork.equals(SocialNetwork.Facebook.toString()) ) {
            FacebookLoginPage.isLoadedLoginPage("test.com","u123");
        } else if (socialNetwork.equals(SocialNetwork.Gmail.toString()) ) {
            GmailLoginPage.isLoadedLoginPage("test.com","u156");
        } else {
            LinkedinLoginPage.isLoadedLoginPage("test.com","u986");
        }
    }

    public static void isLoadedSocialNetworkLoginForm(String socialNetwork) {
        if (socialNetwork.equals(SocialNetwork.Facebook.toString()) ) {
            FacebookLoginPage.isSocialNetworkFormPage();
        } else if (socialNetwork.equals(SocialNetwork.Gmail.toString()) ) {
            GmailLoginPage.isSocialNetworkFormPage();
        } else {
            LinkedinLoginPage.isSocialNetworkFormPage();
        }
    }

    public static void acceptConsent() {
        BaseClass.click(By.id(txtAccept));
    }

    public static void refuseConsent() {
        BaseClass.click(By.id(txtRefuse));
    }

    private static void encodeUserName(String userName) {
        BaseClass.putText(By.id(txtUserName), userName);
    }

    private static void encodePassword(String password) {
        BaseClass.putText(By.id(txtPassword), password);
    }

    private static void clickSignIn() {
        BaseClass.click(By.id(txtSignIn));
    }

    public static void isLoaded(String pageUrl) {

        if (BaseClass.getCurrentUrl().contains(pageUrl)) {
            System.out.println("The url is correct");
        } else {
            System.out.println("url is incorrect");
        }
    }

    public static void verifyUserStatus(Status status) {
        if (getStatusUser().equals(status.toString())) {
            System.out.println("User Status is activated");
        } else {
            System.out.println("User Status is desactivated");
            throw new RuntimeException("User Status is desactivated");
        }
    }

    public static void verifyUserIdentifier() {
        if (!getIdentifier().isEmpty()) {
            System.out.println("User's Identifier is not null");
        } else {
            System.out.println("User's Identifier is null");
            throw new RuntimeException("User's Identifier is null");
        }
    }

    public static void verifyUserLastName(User user) {
        if (getLastName().equals(user.getLastName().value())) {
            System.out.println("User Last Name is correct");
        } else {
        System.out.println("User Last Name is incorrect");
        throw new RuntimeException("User Last Name is incorrect");
        }
    }



    private static String getStatusUser() {
        return BaseClass.getText(By.xpath(txtStatusUser));
    }

    private static String getIdentifier() {
        return BaseClass.getText(By.xpath(txtIdentifier));
    }

    private static String getLastName() {
        return BaseClass.getText(By.xpath(txtLastName));
    }

    private static String getFirstName() {
        return BaseClass.getText(By.xpath(txtFirstName));
    }

    public static String getUserName() {
        return BaseClass.getText(By.xpath(txtUserName));
    }

    private static String getEmail() {
        return BaseClass.getText(By.xpath(txtEmail));
    }

    public static String getCreatedAt() {
        return BaseClass.getText(By.xpath(txtCreatedAt));
    }
}
