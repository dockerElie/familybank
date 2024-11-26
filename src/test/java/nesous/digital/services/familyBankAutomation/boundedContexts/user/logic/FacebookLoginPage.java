package nesous.digital.services.familyBankAutomation.boundedContexts.user.logic;

import nesous.digital.services.familyBankAutomation.common.BaseClass;
import org.openqa.selenium.By;

public class FacebookLoginPage {

    private static final String pageUrl = "facebook.com/login";

    private static final String txtEmail = "email";
    private static final String txtPassword = "pass";
    private static final String txtSignIn = "loginbutton";
    private static final String txtAcceptFacebook = "";
    private static final String txtRefuseFacebook = "";
    public static void isLoadedLoginPage(String email , String password) {
        isSocialNetworkFormPage();
        encodeEmail(email);
        encodePassword(password);
        clickSignIn();
    }

    public static void isSocialNetworkFormPage() {
        if (BaseClass.getCurrentUrl().contains(pageUrl)) {
            System.out.println("The url is correct");
        } else {
            throw new RuntimeException();
        }
    }

    private static void encodeEmail(String email) {
        BaseClass.putText(By.id(txtEmail), email);
    }

    private static void encodePassword(String password) {
        BaseClass.putText(By.id(txtPassword), password);
    }

    private static void clickSignIn() {
        BaseClass.click(By.id(txtSignIn));
    }

    public static void acceptConsentFacebook() {
        BaseClass.click(By.xpath(txtAcceptFacebook));
    }

    public static void RefuseConsentFacebook() {
        BaseClass.click(By.xpath(txtRefuseFacebook));
    }

}
