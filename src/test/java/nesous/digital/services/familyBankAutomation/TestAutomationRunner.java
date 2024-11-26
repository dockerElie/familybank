package nesous.digital.services.familyBankAutomation;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        plugin = {"pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json"
        },
        features = {"src/test/resources/features/Login/authenticated_user.feature"},
        glue = {"nesous.digital.services.familyBankAutomation.boundedContexts.user.testcases"}
        //tags = "@regression"
)

public class TestAutomationRunner extends AbstractTestNGCucumberTests {
}
