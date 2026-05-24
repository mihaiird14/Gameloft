package ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
//tests to test the behavior of the page with an invalid and valid id
public class GamePageTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "https://play.ludigames.com";

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    //function for cookie popUp
    private void acceptCookies() {
        try {
            WebElement acceptButton = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(
                            By.id("didomi-notice-agree-button")
                    ));
            acceptButton.click();
            System.out.println("Cookies popup accepted.");
        } catch (Exception e) {
            System.out.println("No cookies popup found, continuing...");
        }
    }

    @Test
    @DisplayName("TC-UI-02: Valid game ID")
    public void testValidGameIdLoadsCorrectly() {
        driver.get(BASE_URL + "/game.html?pID=8089"); //valid id
        acceptCookies();
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = driver.getCurrentUrl();

        System.out.println("=== TC-UI-02 Results ===");
        System.out.println("URL: " + currentUrl);
        System.out.println("Title: " + driver.getTitle());

        assertFalse(currentUrl.contains("index.html"),
                "Valid game ID should NOT redirect to homepage. URL: " + currentUrl);

        assertFalse(driver.getTitle().isEmpty(),
                "Game page should have a title");
    }

    @Test
    @DisplayName("TC-UI-03: Non-existent game ID redirects to homepage")
    public void testNonExistentGameIdRedirectsToHomepage() {
        driver.get(BASE_URL + "/game.html?pID=99999999");
        acceptCookies();
        try { Thread.sleep(5000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = driver.getCurrentUrl();

        System.out.println("=== TC-UI-03 Results ===");
        System.out.println("URL după ID inexistent: " + currentUrl);

        boolean redirectedToHome = currentUrl.contains("index.html")
                || currentUrl.equals(BASE_URL + "/")
                || currentUrl.equals(BASE_URL);

        assertTrue(redirectedToHome,
                "Non-existent game ID should redirect to homepage. " +
                        "Actual URL: " + currentUrl);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}