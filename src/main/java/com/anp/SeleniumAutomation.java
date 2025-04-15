package com.anp;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Proxy;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SeleniumAutomation {

    static final String VIDEO_URL = "";
    static final int NUM_TABS = 50;

    static final List<String> PROXIES = Arrays.asList(
            "165.227.192.216:80",
            "134.209.29.120:3128",
            "103.169.255.56:3127",
            "213.136.101.40:3128"
    );

    static final List<String> USER_AGENTS = Arrays.asList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/122.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_2) AppleWebKit/605.1.15 Version/16.0 Safari/605.1.15",
            "Mozilla/5.0 (Linux; Android 11; SM-A515F) AppleWebKit/537.36 Chrome/88.0.4324.93 Mobile Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 16_1 like Mac OS X) AppleWebKit/605.1.15 Mobile/15E148"
    );

    public static void main(String[] args) throws InterruptedException {

        // ✅ Set ChromeDriver path here
        System.setProperty("webdriver.chrome.driver", "/Users/anooptiwari/workspace/workspace/intellij-workspace/mvn-test/automation/chromedriver");

        for (int i = 0; i < NUM_TABS; i++) {
            int finalI = i + 1;
            new Thread(() -> {
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
                    driver.get(VIDEO_URL);

                    System.out.println("[" + finalI + "] Opened with proxy: " + proxyAddr);
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
            }).start();

            Thread.sleep(1000);
        }
    }
}
