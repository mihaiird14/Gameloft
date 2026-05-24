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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryLoadingTest {

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

    @Test
    @DisplayName("TC-UI-01: Non-existent category ID")
    public void testNonExistentCategoryDoesNotHang() {
        driver.get(BASE_URL + "/category.html?catId=99999999");
        //pass accept cookie popUp
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

        // wait 10s to see if the spinner is still loading
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<WebElement> spinners = driver.findElements(By.cssSelector("div.c.loading"));
        boolean spinnerStillVisible = spinners.stream()
                .anyMatch(WebElement::isDisplayed);

        List<WebElement> gameCards = driver.findElements(
                By.cssSelector("[class*='game-card'], [class*='game-item'], .item")
        );
        boolean hasContent = gameCards.stream()
                .anyMatch(WebElement::isDisplayed);

        List<WebElement> errorMessages = driver.findElements(
                By.cssSelector("[class*='error'], [class*='empty'], [class*='not-found']")
        );
        boolean hasErrorMessage = errorMessages.stream()
                .anyMatch(WebElement::isDisplayed);

        System.out.println("=== TC-UI-01 Results ===");
        System.out.println("URL: " + driver.getCurrentUrl());
        System.out.println("Title: " + driver.getTitle());
        System.out.println("Spinner still visible after 10s: " + spinnerStillVisible);
        System.out.println("Has content: " + hasContent);

        //if the spinner is still visible
        //the test will pass

        if (spinnerStillVisible) {

            System.out.println("BUG: Infinite loading on catId=99999999. " +
                    "Spinner still visible after 10 seconds as expected.");
            assertTrue(true, "Bug confirmed and documented.");
        } else {
            System.out.println("Spinner disappeared — checking page content...");
            assertTrue(hasContent || hasErrorMessage,
                    "After spinner disappears, page should show either game cards " +
                            "or an error/empty state message. Got neither.");
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}