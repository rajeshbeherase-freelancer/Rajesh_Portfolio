package Tests;

import Baseclasses.BaseClass;
import PageClasses.AlertPoliciesPage;
import PageClasses.LoginPage;
import Utils.ConfigReader;
import org.testng.annotations.Test;

public class AlertPoliciesTest extends BaseClass {

    @Test
    public void deleteAlertPolicyTest() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.getValidUsername(), ConfigReader.getValidPassword());

        AlertPoliciesPage alertPage = new AlertPoliciesPage(driver);
        alertPage.clickBehaviour()
                .clickAlertPolicies()
                .deleteAlertPolicy();
    }
}
