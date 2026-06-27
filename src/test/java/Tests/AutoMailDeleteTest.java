package Tests;

import Baseclasses.BaseClass;
import PageClasses.AutoMailPage;
import PageClasses.LoginPage;
import Utils.ConfigReader;
import org.testng.annotations.Test;

public class AutoMailDeleteTest extends BaseClass {

    @Test
    public void autoMailDeleteTest() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.getValidUsername(), ConfigReader.getValidPassword());

        AutoMailPage autoMailPage = new AutoMailPage(driver);
        autoMailPage.clickReports()
                .clickAutoEmailReports()
                .selectEntries("200")
                .deleteAllReports();
    }
}
