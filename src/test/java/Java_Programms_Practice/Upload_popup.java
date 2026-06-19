package Java_Programms_Practice;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class Upload_popup {
	
	@Test 
	public void M1() throws InterruptedException, IOException, URISyntaxException {
		System.out.println("Chandan");
		WebDriver driver=new ChromeDriver();
		driver.get("https://the-internet.herokuapp.com/upload");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		WebDriverWait wait=new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement Upload=wait.until(ExpectedConditions.elementToBeClickable((By.id("drag-drop-upload"))));
		Upload.click();
		Thread.sleep(1000);
		
//		This below line is a common way if we are saving the AutoIT script in our Local System 
//		Runtime.getRuntime().exec("C:\\Users\\chand\\Music\\FileUploadPopup.exe");
		
//		This below line is used to fetch the File from the src/test/resource file 
		String exePath = new File(getClass().getClassLoader().getResource("All_Required_Files/FileUploadPopup.exe").toURI()).getAbsolutePath();

		Runtime.getRuntime().exec(exePath);
		Thread.sleep(5000);
		
		
	}


}
