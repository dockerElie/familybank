package nesous.digital.services.familyBankAutomation.boundedContexts.bankAccount;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import nesous.digital.services.familyBankAutomation.common.BaseClass;

public class CreateBankAccountTestAutomation extends BaseClass {
    @Before
    public void setup(){
        BaseClass.setDriver();
    }

    @Given("^Launch Google Home Page$")
    public void launchGoogle(){
        BaseClass.navigateUrl("https://tutorialsninja.com/demo/index.php?route=account/login");
    }


    @After
    public void teardown(){
        driver.quit();
    }

}
