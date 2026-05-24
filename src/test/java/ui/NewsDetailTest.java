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

//tests to test the behavior of the page on an invalid news id and a valid one

public class NewsDetailTest {

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
    @DisplayName("TC-UI-04: Valid news ID")
    public void testValidNewsIdLoadsContent() {
        // hwpr=mahjong is valid
        driver.get(BASE_URL + "/hw-detail.html?hwpr=mahjong");
        acceptCookies();

        try { Thread.sleep(5000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        List<WebElement> spinners = driver.findElements(By.cssSelector("div.c.loading"));
        boolean spinnerStillVisible = spinners.stream().anyMatch(WebElement::isDisplayed);

        System.out.println("=== TC-UI-04 Results ===");
        System.out.println("URL: " + driver.getCurrentUrl());
        System.out.println("Title: " + driver.getTitle());
        System.out.println("Spinner still visible after 5s: " + spinnerStillVisible);

        assertFalse(spinnerStillVisible,
                "Valid news ID 'mahjong' should load content within 5 seconds, " +
                        "but spinner is still visible.");
    }

    @Test
    @DisplayName("TC-UI-05: Non-existent news ID results in empty page")
    public void testNonExistentNewsIdDoesNotHang() {
        driver.get(BASE_URL + "/hw-detail.html?hwpr=mahjon");
        acceptCookies();

        try { Thread.sleep(10000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        //check title  - should be empty
        WebElement title = driver.findElement(By.cssSelector(".container .title"));
        WebElement intro = driver.findElement(By.cssSelector(".container .intro"));

        String titleText = title.getText().trim();
        String introText = intro.getText().trim();

        System.out.println("=== TC-UI-05 Results ===");
        System.out.println("URL: " + driver.getCurrentUrl());
        System.out.println("Title text: '" + titleText + "'");
        System.out.println("Intro text: '" + introText + "'");
        assertTrue(titleText.isEmpty() && introText.isEmpty(),
                "BUG CONFIRMED: Non-existent news ID 'mahjon' loads empty page " +
                        "with no error message for the user. " +
                        "Title: '" + titleText + "', Intro: '" + introText + "'");
    }
    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}