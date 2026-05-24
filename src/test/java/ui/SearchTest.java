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

public class SearchTest {

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

    private void navigateToSearchPage() {
        WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a.search-btn")
        ));
        searchBtn.click();
        wait.until(ExpectedConditions.urlContains("search.html"));
        System.out.println("On search page: " + driver.getCurrentUrl());
    }

    @Test
    @DisplayName("TC-UI-06: Search with valid game name shows dropdown results")
    public void testValidSearchShowsDropdown() {
        driver.get(BASE_URL);
        acceptCookies();
        navigateToSearchPage();

        // Input
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("search")
        ));
        searchInput.click();
        searchInput.sendKeys("racing");

        // wait for results
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        List<WebElement> results = driver.findElements(
                By.cssSelector("div.search-result a.item")
        );

        WebElement notFoundMsg = driver.findElement(By.cssSelector("div.not-found"));
        boolean notFoundVisible = notFoundMsg.isDisplayed();

        System.out.println("=== TC-UI-06 Results ===");
        System.out.println("Search term: 'racing'");
        System.out.println("Results found: " + results.size());
        System.out.println("Not found message visible: " + notFoundVisible);

        assertFalse(notFoundVisible,
                "Valid search should NOT show 'We don't have that title yet'");
        assertFalse(results.isEmpty(),
                "Valid search for 'racing' should show at least one result in dropdown");
    }

    @Test
    @DisplayName("TC-UI-07: Search with non-existent game")
    public void testInvalidSearchShowsNotFoundMessage() {
        driver.get(BASE_URL);
        acceptCookies();
        navigateToSearchPage();

        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("search")
        ));
        searchInput.click();
        searchInput.sendKeys("xyzgamenotexist99999"); //input

        //wait for result
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        WebElement notFoundMsg = driver.findElement(By.cssSelector("div.not-found"));
        boolean notFoundVisible = notFoundMsg.isDisplayed();

        List<WebElement> results = driver.findElements(
                By.cssSelector("div.search-result a.item")
        );

        System.out.println("=== TC-UI-05 Results ===");
        System.out.println("Search term: 'xyzgamenotexist99999'");
        System.out.println("Not found message visible: " + notFoundVisible);
        System.out.println("Results in dropdown: " + results.size());
        //pass if the not found message is shown
        assertTrue(notFoundVisible,
                "Search for non-existent game should show: " +
                        "'We don't have that title yet. You can try our top 10 games!'");
        assertTrue(results.isEmpty(),
                "Search for non-existent game should have no results in dropdown");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}