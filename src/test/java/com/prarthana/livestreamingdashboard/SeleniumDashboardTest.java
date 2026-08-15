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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SeleniumDashboardTest.class)
public class SeleniumDashboardTest implements TestWatcher {

    private WebDriver driver;

    private final String BASE_URL =
            "http://localhost:8082/LiveStreamingDashboard-0.0.1-SNAPSHOT";

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
    }

    // Helper: scroll to element then click (avoids "element click intercepted")
    private void scrollAndClick(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    // Test Case 1: Dashboard loads
    @Test
    void dashboardLoadsSuccessfully() {

        driver.get(BASE_URL + "/");

        assertTrue(driver.getTitle()
                .contains("Live Streaming Dashboard"));

        assertTrue(driver.getPageSource()
                .contains("Dashboard Overview"));
    }

    // Test Case 2: Add Stream page loads
    @Test
    void addStreamPageLoadsSuccessfully() {

        driver.get(BASE_URL + "/add");

        assertTrue(driver.getTitle()
                .contains("Add Stream"));

        assertTrue(driver.getPageSource()
                .contains("Add New Stream"));
    }

    // Test Case 3: Add a new stream
    @Test
    void addNewStreamSuccessfully() {

        driver.get(BASE_URL + "/add");

        driver.findElement(By.name("streamName"))
                .sendKeys("Selenium Test Stream");

        driver.findElement(By.name("channelName"))
                .sendKeys("Test Channel");

        driver.findElement(By.name("status"))
                .sendKeys("online");

        driver.findElement(By.name("quality"))
                .sendKeys("HD");

        driver.findElement(By.name("location"))
                .sendKeys("Mumbai");

        WebElement submitBtn = driver.findElement(
                By.cssSelector("button[type='submit']"));
        scrollAndClick(submitBtn);

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertTrue(driver.getPageSource()
                .contains("Selenium Test Stream"));
    }

    // Test Case 4: Search stream
    @Test
    void searchStreamWorks() {

        // Ensure the stream exists before searching for it
        addNewStreamSuccessfully();

        driver.get(BASE_URL + "/");

        driver.findElement(By.name("name"))
                .sendKeys("Selenium Test Stream");

        WebElement submitBtn = driver.findElement(
                By.cssSelector("button[type='submit']"));
        scrollAndClick(submitBtn);

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertTrue(driver.getPageSource()
                .contains("Selenium Test Stream"));
    }

    // Test Case 5: Online status filter
    @Test
    void onlineFilterWorks() {

        driver.get(BASE_URL + "/status?value=online");

        assertTrue(driver.getPageSource()
                .contains("online"));
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    // Captures a screenshot automatically when a test fails
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