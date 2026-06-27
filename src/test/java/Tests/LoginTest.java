package Tests;

import Baseclasses.BaseClass;
import PageClasses.LoginPage;
import Utils.DataProviders;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Map;

public class LoginTest extends BaseClass {

    @Test(dataProvider = "loginData", dataProviderClass = DataProviders.class)
    public void loginTest(Map<String, String> data) {
        String scenario = data.get("Scenario");
        String username = data.get("Username");
        String password = data.get("Password");
        String expectedResult = data.get("ExpectedResult");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        if ("Dashboard".equalsIgnoreCase(expectedResult)) {
            wait.until(ExpectedConditions.titleContains("Empmonitor"));
            Assert.assertTrue(driver.getTitle().contains("Empmonitor"),
                    "Valid login failed for scenario: " + scenario);
        } else if ("Error".equalsIgnoreCase(expectedResult)) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    org.openqa.selenium.By.xpath("//*[contains(text(),'incorrect') or contains(text(),'Invalid')]")));
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                    "Error message not displayed for invalid login scenario: " + scenario);
            Assert.assertTrue(loginPage.getErrorMessage().toLowerCase().contains("incorrect"),
                    "Unexpected error message for scenario: " + scenario);
        }
    }

}
