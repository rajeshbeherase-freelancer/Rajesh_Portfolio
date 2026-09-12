package Utils;

import org.testng.annotations.DataProvider;

public class DataProviders {

    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        return ExcelUtils.getTestData("LoginData");
    }

    @DataProvider(name = "employeeData")
    public Object[][] employeeData() {
        return ExcelUtils.getTestData("EmployeeData");
    }
}
