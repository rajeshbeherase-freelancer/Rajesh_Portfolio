package PageClasses;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class EmployeeDetailsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(xpath = "//i[@title='Employees' or @title='Employee']")
    private WebElement employeeMenu;

    @FindBy(xpath = "//*[normalize-space(text())='Employee-Details']")
    private WebElement employeeDetailsOption;

    @FindBy(id = "add_btn")
    private WebElement registerEmployeeOption;

    @FindBy(css = "[placeholder='Enter First Name']")
    private WebElement firstNameTextBox;

    @FindBy(css = "[placeholder='Enter Last Name']")
    private WebElement lastNameTextBox;

    @FindBy(id = "emp_email")
    private WebElement emailTextBox;

    @FindBy(id = "passwd")
    private WebElement passwordField;

    @FindBy(id = "c_passwd")
    private WebElement confirmPasswordField;

    @FindBy(xpath = "(//div[@class='flag-container'])[1]")
    private WebElement countryCodeDropdown;

    @FindBy(xpath = "(//input[@onkeypress='return numbersOnly(event);'])[1]")
    private WebElement mobileTextBox;

    @FindBy(xpath = "(//b[contains(text(),'Employee Code')]/../../input)[1]")
    private WebElement employeeCodeField;

    @FindBy(id = "locations-addEmp")
    private WebElement locationDropdown;

    @FindBy(xpath = "//*[@id=\"emp-register\"]/div[1]/div/div[9]/div/span[1]/span[1]/span/ul")
    private List<WebElement> roleOptions;

    @FindBy(name = "departement_name")
    private WebElement departmentDropdown;

    @FindBy(id = "timeZoneAppend")
    private WebElement timeZoneDropdown;

    @FindBy(id = "Shiftfilteradd")
    private WebElement shiftDropdown;

    @FindBy(id = "EmpReg_departments")
    private WebElement registerEmployeeButton;

    public EmployeeDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public EmployeeDetailsPage openEmployeeDetails() {
        wait.until(ExpectedConditions.elementToBeClickable(employeeMenu)).click();
        wait.until(ExpectedConditions.elementToBeClickable(employeeDetailsOption)).click();
        return this;
    }

    public EmployeeDetailsPage clickRegisterEmployee() {
        wait.until(ExpectedConditions.elementToBeClickable(registerEmployeeOption)).click();
        return this;
    }

    public EmployeeDetailsPage enterFirstName(String firstName) {
        wait.until(ExpectedConditions.elementToBeClickable(firstNameTextBox)).sendKeys(firstName);
        return this;
    }

    public EmployeeDetailsPage enterLastName(String lastName) {
        wait.until(ExpectedConditions.elementToBeClickable(lastNameTextBox)).sendKeys(lastName);
        return this;
    }

    public EmployeeDetailsPage enterEmail(String email) {
        wait.until(ExpectedConditions.elementToBeClickable(emailTextBox)).sendKeys(email);
        return this;
    }

    public EmployeeDetailsPage enterPassword(String password) {
        wait.until(ExpectedConditions.elementToBeClickable(passwordField)).sendKeys(password);
        return this;
    }

    public EmployeeDetailsPage enterConfirmPassword(String password) {
        wait.until(ExpectedConditions.elementToBeClickable(confirmPasswordField)).sendKeys(password);
        return this;
    }

    public EmployeeDetailsPage selectCountryCode(String code) {
        wait.until(ExpectedConditions.elementToBeClickable(countryCodeDropdown)).click();
        String optionXpath = "//li[@data-dial-code='" + code + "']";
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(optionXpath))).click();
        return this;
    }

    public EmployeeDetailsPage enterMobile(String mobile) {
        wait.until(ExpectedConditions.elementToBeClickable(mobileTextBox)).sendKeys(mobile);
        return this;
    }

    public EmployeeDetailsPage enterEmployeeCode(String code) {
        wait.until(ExpectedConditions.elementToBeClickable(employeeCodeField)).sendKeys(code);
        return this;
    }

    public EmployeeDetailsPage selectLocation(String location) {
        new Select(wait.until(ExpectedConditions.elementToBeClickable(locationDropdown))).selectByVisibleText(location);
        return this;
    }

    public EmployeeDetailsPage selectRole(String role) {
        for (WebElement singleRole : roleOptions) {
            if (singleRole.getText().contains(role)) {
                wait.until(ExpectedConditions.elementToBeClickable(singleRole)).click();
                break;
            }
        }
        return this;
    }

    public EmployeeDetailsPage selectDepartment(String department) {
        new Select(wait.until(ExpectedConditions.elementToBeClickable(departmentDropdown))).selectByVisibleText(department);
        return this;
    }

    public EmployeeDetailsPage selectTimeZone(String timeZone) {
        if (timeZone != null && !timeZone.trim().isEmpty()) {
            new Select(wait.until(ExpectedConditions.elementToBeClickable(timeZoneDropdown))).selectByVisibleText(timeZone);
        }
        return this;
    }

    public EmployeeDetailsPage selectShift(String shift) {
        if (shift != null && !shift.trim().isEmpty()) {
            new Select(wait.until(ExpectedConditions.elementToBeClickable(shiftDropdown))).selectByVisibleText(shift);
        }
        return this;
    }

    public void clickRegisterEmployeeButton() {
        wait.until(ExpectedConditions.elementToBeClickable(registerEmployeeButton)).click();
    }
}
