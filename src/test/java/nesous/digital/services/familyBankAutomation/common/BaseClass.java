package nesous.digital.services.familyBankAutomation.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class BaseClass {

    public static WebDriver driver;

    public static void setDriver(){
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
    }
    public static void navigateUrl(String url){
        driver.get(url);
    }

    public static void putText(By locator , String text){
        driver.findElement(locator).sendKeys(text);
    }

    public static void click(By locator){
        driver.findElement(locator).click();
    }

    public static String getText(By locator){
        return driver.findElement(locator).getText();
    }

    public static String getCurrentUrl(){
        return driver.getCurrentUrl();
    }
}
