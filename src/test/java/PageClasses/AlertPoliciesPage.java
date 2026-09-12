package PageClasses;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AlertPoliciesPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(xpath = "//i[@title='Behaviour']")
    private WebElement behaviourMenu;

    @FindBy(xpath = "//*[contains(text(),'Alert Policies')]")
    private WebElement alertPoliciesOption;

    @FindBy(xpath = "//table//a[contains(@title,'Delete') or contains(@class,'delete')] | //table//i[contains(@class,'delete') or contains(@title,'Delete')]")
    private WebElement deletePolicyIcon;

    @FindBy(xpath = "//button[contains(text(),'Yes') or contains(text(),'Delete')]")
    private WebElement confirmDeleteButton;

    public AlertPoliciesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public AlertPoliciesPage clickBehaviour() {
        wait.until(ExpectedConditions.elementToBeClickable(behaviourMenu)).click();
        return this;
    }

    public AlertPoliciesPage clickAlertPolicies() {
        wait.until(ExpectedConditions.elementToBeClickable(alertPoliciesOption)).click();
        return this;
    }

    public void deleteAlertPolicy() {
        wait.until(ExpectedConditions.elementToBeClickable(deletePolicyIcon)).click();
        wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton)).click();
    }
}
