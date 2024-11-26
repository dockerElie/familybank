package nesous.digital.services.familyBankAutomation.boundedContexts.user.logic;

import nesous.digital.services.familyBankAutomation.common.BaseClass;
import org.openqa.selenium.By;

public class LinkedinLoginPage {

    private static final String pageUrl = "linkedin.com/login";

    private static final String txtEmail = "username";
    private static final String txtPassword = "password";
    private static final String txtSignIn = "//div[@id='organic-div']/form/div[3]/button";
    private static final String txtAcceptLinkedin = "";
    private static final String txtRefuseLinkedin = "";
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
    public static void acceptConsentLinkedin() {
        BaseClass.click(By.xpath(txtAcceptLinkedin));
    }

    public static void RefuseConsentLinkedin() {
        BaseClass.click(By.xpath(txtRefuseLinkedin));
    }
}
