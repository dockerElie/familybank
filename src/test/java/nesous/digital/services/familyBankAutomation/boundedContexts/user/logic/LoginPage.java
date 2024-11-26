package nesous.digital.services.familyBankAutomation.boundedContexts.user.logic;

import nesous.digital.services.familyBankAutomation.boundedContexts.user.data.object.SocialNetwork;
import nesous.digital.services.familyBankAutomation.common.BaseClass;
import org.openqa.selenium.By;

public class LoginPage {

    protected static String txtLogin = "login";
    protected static String txtFacebook = "facebook";
    protected static String txtGmail = "gmail";
    protected static String txtLinkedin = "linkedin";


    public static void login() {
        BaseClass.click(By.id(txtLogin));
    }

    public static void selectSocialNetwork(String socialNetwork) {
        if (socialNetwork.equals(SocialNetwork.Facebook.toString()) ) {
            BaseClass.click(By.id(txtFacebook));
        } else if (socialNetwork.equals(SocialNetwork.Gmail.toString()) ) {
            BaseClass.click(By.id(txtGmail));
        } else {
            BaseClass.click(By.id(txtLinkedin));
        }
    }

    public static void selectFacebookSocialNetwork() {
        BaseClass.click(By.id(txtFacebook));
    }

    public static void selectGmailSocialNetwork() {
        BaseClass.click(By.id(txtGmail));
    }

    public static void selectLinkedinSocialNetwork() {
        BaseClass.click(By.id(txtLinkedin));
    }


}
