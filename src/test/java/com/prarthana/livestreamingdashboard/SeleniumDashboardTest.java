package com.prarthana.livestreamingdashboard;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SeleniumDashboardTest.class)
public class SeleniumDashboardTest implements TestWatcher {

    private WebDriver driver;

    // Matches the active port 8082 from your dashboard
    private final String BASE_URL = "http://localhost:8082/LiveStreamingDashboard-0.0.1-SNAPSHOT";

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
    }

    private void scrollAndClick(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    // Test Case 1: Dashboard loads
    @Test
    void dashboardLoadsSuccessfully() {
        driver.get(BASE_URL + "/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertTrue(driver.getTitle().contains("Live Streaming Dashboard"),
                "Title did not match. Current title: " + driver.getTitle() + ", URL: " + driver.getCurrentUrl());

        assertTrue(driver.getPageSource().contains("Dashboard Overview"));
    }

    // Test Case 2: Add Stream page loads via UI button
    // Test Case 2: Add Stream page loads via UI button
    @Test
    void addStreamPageLoadsSuccessfully() {
        driver.get(BASE_URL + "/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Use partialLinkText or normalize-space to ignore HTML whitespace/newlines
        WebElement addStreamBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[contains(normalize-space(.), 'Add Stream') and (self::a or self::button)]")
                )
        );
        scrollAndClick(addStreamBtn);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertTrue(driver.getPageSource().toLowerCase().contains("add"),
                "Add stream page did not load. URL: " + driver.getCurrentUrl());
    }

    // Test Case 3: Add a new stream
    @Test
    void addNewStreamSuccessfully() {
        driver.get(BASE_URL + "/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement addStreamBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[contains(normalize-space(.), 'Add Stream') and (self::a or self::button)]")
                )
        );
        scrollAndClick(addStreamBtn);

        WebElement streamNameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("streamName"))
        );
        streamNameInput.sendKeys("Selenium Test Stream");

        driver.findElement(By.name("channelName")).sendKeys("Test Channel");
        driver.findElement(By.name("status")).sendKeys("online");
        driver.findElement(By.name("quality")).sendKeys("HD");
        driver.findElement(By.name("location")).sendKeys("Mumbai");

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        scrollAndClick(submitBtn);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertTrue(driver.getPageSource().contains("Selenium Test Stream"));
    }

    // Test Case 4: Search stream
    @Test
    void searchStreamWorks() {
        addNewStreamSuccessfully();

        driver.get(BASE_URL + "/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input[contains(@placeholder,'stream name') or @name='streamName' or @name='name']")
                )
        );

        searchInput.clear();
        searchInput.sendKeys("Selenium Test Stream");

        WebElement searchBtn = driver.findElement(
                By.xpath("//button[contains(normalize-space(.), 'Search') or @type='submit']")
        );
        scrollAndClick(searchBtn);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertTrue(driver.getPageSource().contains("Selenium Test Stream"));
    }

    // Test Case 5: Online status filter via UI button
    @Test
    void onlineFilterWorks() {
        driver.get(BASE_URL + "/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement onlineBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Online')] | //a[contains(text(),'Online')]")
                )
        );
        scrollAndClick(onlineBtn);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertTrue(driver.getPageSource().toLowerCase().contains("online"));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (driver != null) {
            try {
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File destDir = new File("target/screenshots");
                if (!destDir.exists()) destDir.mkdirs();
                File dest = new File(destDir, context.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_") + ".png");
                Files.copy(screenshot.toPath(), dest.toPath());
                System.out.println("Screenshot saved: " + dest.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Could not save screenshot: " + e.getMessage());
            }
        }
    }
}