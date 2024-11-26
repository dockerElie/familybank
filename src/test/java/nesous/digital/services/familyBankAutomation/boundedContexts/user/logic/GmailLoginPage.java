package nesous.digital.services.familyBankAutomation.boundedContexts.user.logic;

import nesous.digital.services.familyBankAutomation.common.BaseClass;
import org.openqa.selenium.By;

public class GmailLoginPage {

    private static final String pageUrl = "accounts.google.com";

    private static final String txtEmail = "identifierId";

    private static final String txtNext = "//span[text()='Suivant']";    // French version of Gmail
    private static final String txtPassword = "pass";
    private static final String txtSignIn = "//div[@id='password']/div[1]/div/div[1]/input";
    private static final String txtAcceptGmail = "//span[text()='Continuer']/parent::button";  // French version of Gmail
    private static final String txtRefuseGmail = "//span[text()='Annuler']/parent::button";  // French version of Gmail


    public static void isLoadedLoginPage(String email , String password) {
        isSocialNetworkFormPage();
        encodeEmail(email);
        clickNext();
        encodePassword(password);
        clickNext();
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

    private static void clickNext() { BaseClass.click(By.xpath(txtNext)); }

    private static void encodePassword(String password) {
        BaseClass.putText(By.xpath(txtPassword), password);
    }

    private static void clickSignIn() {
        BaseClass.click(By.id(txtSignIn));
    }

    public static void acceptConsentGmail() {
        BaseClass.click(By.xpath(txtAcceptGmail));
    }

    public static void RefuseConsentGmail() {
        BaseClass.click(By.xpath(txtRefuseGmail));
    }

}
