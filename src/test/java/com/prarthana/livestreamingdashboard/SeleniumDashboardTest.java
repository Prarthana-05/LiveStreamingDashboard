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

    // FIX 1: Standalone Tomcat uses port 8080 by default, NOT 8082
    private final String BASE_URL =
            "http://localhost:8080/LiveStreamingDashboard-0.0.1-SNAPSHOT";

    @BeforeEach
    void setUp() {
        // FIX 2: Enable headless mode so Jenkins can execute Chrome without a desktop session
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
    }

    private void scrollAndClick(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    @Test
    void dashboardLoadsSuccessfully() {
        driver.get(BASE_URL + "/");

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertTrue(driver.getTitle().contains("Live Streaming Dashboard"),
                "Title did not match. Current title: " + driver.getTitle() + ", URL: " + driver.getCurrentUrl());

        assertTrue(driver.getPageSource().contains("Dashboard Overview"));
    }

    @Test
    void addStreamPageLoadsSuccessfully() {
        driver.get(BASE_URL + "/add");

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertTrue(driver.getTitle().contains("Add Stream"),
                "Title did not match. Current title: " + driver.getTitle() + ", URL: " + driver.getCurrentUrl());

        assertTrue(driver.getPageSource().contains("Add New Stream"));
    }

    @Test
    void addNewStreamSuccessfully() {
        driver.get(BASE_URL + "/add");

        // FIX 3: Explicit wait to make sure form input is actually loaded before interacting
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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

    @Test
    void searchStreamWorks() {
        addNewStreamSuccessfully();

        driver.get(BASE_URL + "/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("name"))
        );

        nameInput.sendKeys("Selenium Test Stream");

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        scrollAndClick(submitBtn);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertTrue(driver.getPageSource().contains("Selenium Test Stream"));
    }

    @Test
    void onlineFilterWorks() {
        driver.get(BASE_URL + "/status?value=online");

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertTrue(driver.getPageSource().contains("online"));
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