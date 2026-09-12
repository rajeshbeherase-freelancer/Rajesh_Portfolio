package PageClasses;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AutoMailPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(xpath = "//i[@title='Reports']")
    private WebElement reportsMenu;

    @FindBy(xpath = "//*[normalize-space(text())='Auto Email Reports']")
    private WebElement autoEmailReportsLink;

    @FindBy(id = "ShowEntriesList")
    private WebElement showEntriesDropdown;

    @FindBy(xpath = "//i[@title='Delete Report']")
    private List<WebElement> deleteIcons;

    @FindBy(xpath = "//button[text()='Yes, delete it!']")
    private WebElement confirmDeleteButton;

    public AutoMailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public AutoMailPage clickReports() {
        wait.until(ExpectedConditions.elementToBeClickable(reportsMenu)).click();
        return this;
    }

    public AutoMailPage clickAutoEmailReports() {
        wait.until(ExpectedConditions.elementToBeClickable(autoEmailReportsLink)).click();
        return this;
    }

    public AutoMailPage selectEntries(String value) {
        Select select = new Select(wait.until(ExpectedConditions.visibilityOf(showEntriesDropdown)));
        select.selectByVisibleText(value);
        return this;
    }

    public void deleteAllReports() {
        while (true) {
            List<WebElement> deleteButtons = deleteIcons;
            if (deleteButtons.isEmpty()) {
                System.out.println("No more reports to delete.");
                break;
            }

            wait.until(ExpectedConditions.elementToBeClickable(deleteButtons.get(0))).click();
            wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton)).click();
            wait.until(ExpectedConditions.invisibilityOf(confirmDeleteButton));
        }
    }
}
