package Tests;

import Baseclasses.BaseClass;
import PageClasses.EmployeeDetailsPage;
import PageClasses.LoginPage;
import Utils.ConfigReader;
import Utils.DataProviders;
import org.testng.annotations.Test;

import java.util.Map;

public class EmployeeRegistrationTest extends BaseClass {

    @Test(dataProvider = "employeeData", dataProviderClass = DataProviders.class)
    public void registerEmployeeTest(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.getValidUsername(), ConfigReader.getValidPassword());

        EmployeeDetailsPage employeePage = new EmployeeDetailsPage(driver);
        employeePage.openEmployeeDetails()
                .clickRegisterEmployee()
                .enterFirstName(data.get("FirstName"))
                .enterLastName(data.get("LastName"))
                .enterEmail(data.get("Email"))
                .enterPassword(data.get("Password"))
                .enterConfirmPassword(data.get("Password"))
                .selectCountryCode(data.get("CountryCode"))
                .enterMobile(data.get("Mobile"))
                .enterEmployeeCode(data.get("EmployeeCode"))
                .selectLocation(data.get("Location"))
                .selectRole(data.get("Role"))
                .selectDepartment(data.get("Department"))
                .selectTimeZone(data.get("TimeZone"))
                .selectShift(data.get("Shift"))
                .clickRegisterEmployeeButton();
    }
}
