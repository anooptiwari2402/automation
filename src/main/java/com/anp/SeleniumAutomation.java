package com.anp;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SeleniumAutomation {
    private static final int NUM_TABS = 10;
    private static final List<String> PROXIES = Arrays.asList(
            "165.227.192.216:80",
            "134.209.29.120:3128",
            "103.169.255.56:3127",
            "213.136.101.40:3128"
    );
    private static final List<String> USER_AGENTS = Arrays.asList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/122.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_2) AppleWebKit/605.1.15 Version/16.0 Safari/605.1.15",
            "Mozilla/5.0 (Linux; Android 11; SM-A515F) AppleWebKit/537.36 Chrome/88.0.4324.93 Mobile Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 16_1 like Mac OS X) AppleWebKit/605.1.15 Mobile/15E148"
    );
    private static final String VIDEO_URL = "https://www.youtube.com/shorts/dZsI9edsjtk"; // Example URL

    public static void main(String[] args) throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\anotiwar\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        for (int i = 0; i < NUM_TABS; i++) {
            int finalI = i + 1;
            executorService.submit(() -> {
                WebDriver driver = null;
                try {
                    Random rand = new Random();
                    String proxyAddr = PROXIES.get(rand.nextInt(PROXIES.size()));
                    String userAgent = USER_AGENTS.get(rand.nextInt(USER_AGENTS.size()));

                    Proxy proxy = new Proxy();
                    proxy.setHttpProxy(proxyAddr);

                    ChromeOptions options = new ChromeOptions();
                    options.setProxy(proxy);
                    options.addArguments("--user-agent=" + userAgent);
                    options.addArguments("--disable-blink-features=AutomationControlled");
                    options.addArguments("--disable-notifications");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    // Uncomment if you want no UI: options.addArguments("--headless=new");

                    driver = new ChromeDriver(options);
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // CHANGE: Added missing wait variable declaration

                    driver.get(VIDEO_URL);

                    System.out.println("[" + finalI + "] Opened with proxy: " + proxyAddr);
                    handleYouTubeConsent(driver, wait, finalI);

                    // Wait for video element and interact
                    WebElement video = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("video")));
                    Actions action = new Actions(driver);
                    action.moveToElement(video).click().perform();

                    Thread.sleep(3000);

                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    for (int s = 0; s < 10; s++) {
                        js.executeScript("window.scrollBy(0, 100);");
                        Thread.sleep(2000);
                    }

                    Thread.sleep(25000); // Total ~45s

                } catch (Exception e) {
                    System.err.println("[" + finalI + "] Error: " + e.getMessage());
                } finally {
                    if (driver != null) {
                        driver.quit();
                    }
                    System.out.println("[" + finalI + "] Tab closed.");
                }
            });

            Thread.sleep(1000);
        }

        executorService.shutdown();
    }
    private static void handleYouTubeConsent(WebDriver driver, WebDriverWait wait, int tabNumber) {
        try {
            // Multiple selectors for different consent popup variations
            List<String> consentSelectors = Arrays.asList(
                    "button[aria-label*='Accept all']",
                    "button[aria-label*='Accept All']",
                    "button:contains('Accept all')",
                    "button:contains('Accept All')",
                    "[data-testid='accept-all-button']",
                    ".VfPpkd-LgbsSe[jsname='V67aGc']", // Google's material design button
                    "ytd-button-renderer button[aria-label*='Accept']"
            );

            for (String selector : consentSelectors) {
                try {
                    WebElement consentButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
                    if (consentButton.isDisplayed()) {
                        consentButton.click();
                        System.out.println("[" + tabNumber + "] Consent accepted using selector: " + selector);
                        Thread.sleep(2000); // Wait for consent processing
                        return;
                    }
                } catch (TimeoutException | NoSuchElementException e) {
                    // Continue to next selector
                    continue;
                }
            }

            // Fallback: Try to find any button containing "accept" text (case-insensitive)
            try {
                WebElement fallbackButton = driver.findElement(By.xpath("//button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'accept')]"));
                if (fallbackButton.isDisplayed()) {
                    fallbackButton.click();
                    System.out.println("[" + tabNumber + "] Consent accepted using fallback xpath");
                    Thread.sleep(2000);
                    return;
                }
            } catch (NoSuchElementException e) {
                // No consent popup found, proceed normally
                System.out.println("[" + tabNumber + "] No consent popup detected, proceeding...");
            }

        } catch (Exception e) {
            System.out.println("[" + tabNumber + "] Consent handling failed, proceeding anyway: " + e.getMessage());
        }
    }
}