package com.authenhub.service.impl;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.Proxy;
import jakarta.annotation.PreDestroy;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for web scraping using Playwright
 * Provides optimized browser configuration and anti-scraping measures
 */
@Service
@Slf4j
public class PlaywrightScraperService {

    @Value("${scraper.proxy.enabled:false}")
    private boolean proxyEnabled;

    @Value("${scraper.proxy.host:}")
    private String proxyHost;

    @Value("${scraper.proxy.port:0}")
    private int proxyPort;

    @Value("${scraper.proxy.username:}")
    private String proxyUsername;

    @Value("${scraper.proxy.password:}")
    private String proxyPassword;

    @Value("${scraper.headless:true}")
    private boolean headless;

    @Value("${scraper.user-agent-rotation.enabled:true}")
    private boolean userAgentRotationEnabled;

    private static final List<String> USER_AGENTS = Arrays.asList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Safari/605.1.15",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36 Edg/133.0.2623.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:124.0) Gecko/20100101 Firefox/124.0",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36"
    );

    private Playwright playwright;
    private Browser browser;
    private final Object browserLock = new Object();

    public PlaywrightScraperService() {
        initializePlaywright();
    }

    /**
     * Initialize Playwright instance
     */
    private void initializePlaywright() {
        try {
            log.info("Initializing Playwright");
            this.playwright = Playwright.create();
            initializeBrowser();
        } catch (Exception e) {
            log.error("Failed to initialize Playwright: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Playwright", e);
        }
    }

    /**
     * Initialize the browser with optimized settings
     */
    private void initializeBrowser() {
        synchronized (browserLock) {
            try {
                if (browser != null && browser.isConnected()) {
                    log.info("Browser already initialized and connected");
                    return;
                }

                log.info("Initializing browser with headless={}", headless);
                BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                        .setHeadless(headless)
                        .setArgs(Arrays.asList(
                                "--disable-gpu",
                                "--disable-dev-shm-usage",
                                "--disable-setuid-sandbox",
                                "--no-sandbox",
                                "--disable-accelerated-2d-canvas",
                                "--disable-notifications",
                                "--disable-extensions",
                                "--disable-infobars",
                                "--disable-web-security",
                                "--disable-features=site-per-process",
                                "--disable-popup-blocking",
                                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36"
                        ))
                        .setTimeout(120000); // Increase timeout to 2 minutes

                // Add proxy if enabled
                if (proxyEnabled && !proxyHost.isEmpty() && proxyPort > 0) {
                    Proxy proxy = new Proxy(proxyHost + ":" + proxyPort);
                    if (!proxyUsername.isEmpty() && !proxyPassword.isEmpty()) {
                        proxy.setUsername(proxyUsername);
                        proxy.setPassword(proxyPassword);
                    }
                    launchOptions.setProxy(proxy);
                }

                this.browser = playwright.chromium().launch(launchOptions);
                log.info("Playwright browser initialized successfully");
            } catch (Exception e) {
                log.error("Failed to initialize Playwright browser: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to initialize Playwright browser", e);
            }
        }
    }

    /**
     * Create a new browser context with optimized settings
     * @return Browser context
     */
    public BrowserContext createContext() {
        synchronized (browserLock) {
            try {
                // Ensure browser is initialized
                if (browser == null || !browser.isConnected()) {
                    log.info("Browser is not initialized or not connected, reinitializing...");
                    initializeBrowser();
                }

                Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                        .setJavaScriptEnabled(true)
                        .setUserAgent(getRandomUserAgent())
                        .setViewportSize(1920, 1080)
                        .setDeviceScaleFactor(1.0)
                        .setHasTouch(false)
                        .setIsMobile(false)
                        .setLocale("en-US")
                        .setTimezoneId("Asia/Ho_Chi_Minh")
                        .setBypassCSP(true) // Bypass Content Security Policy
                        .setIgnoreHTTPSErrors(true); // Ignore HTTPS errors

                // Set additional headers to mimic a real browser
                Map<String, String> extraHeaders = new HashMap<>();
                extraHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
                extraHeaders.put("Accept-Language", "en-US,en;q=0.9");
                extraHeaders.put("Accept-Encoding", "gzip, deflate, br");
                extraHeaders.put("Connection", "keep-alive");
                extraHeaders.put("Upgrade-Insecure-Requests", "1");
                extraHeaders.put("Sec-Fetch-Dest", "document");
                extraHeaders.put("Sec-Fetch-Mode", "navigate");
                extraHeaders.put("Sec-Fetch-Site", "none");
                extraHeaders.put("Sec-Fetch-User", "?1");
                extraHeaders.put("Cache-Control", "max-age=0");

                contextOptions.setExtraHTTPHeaders(extraHeaders);

                log.info("Creating new browser context");
                return browser.newContext(contextOptions);
            } catch (Exception e) {
                log.error("Error creating browser context: {}", e.getMessage(), e);
                // Try to reinitialize everything
                try {
                    close();
                    initializePlaywright();
                    log.info("Reinitialized Playwright after context creation error");
                    return browser.newContext(new Browser.NewContextOptions().setUserAgent(getRandomUserAgent()));
                } catch (Exception e2) {
                    log.error("Failed to recover from context creation error: {}", e2.getMessage(), e2);
                    throw new RuntimeException("Failed to create browser context", e2);
                }
            }
        }
    }

    /**
     * Navigate to a URL with optimized settings and anti-scraping measures
     * @param url URL to navigate to
     * @param resourceTypesToBlock Resource types to block (e.g., image, stylesheet)
     * @param pageConsumer Consumer to process the page
     * @param <T> Return type
     * @return Result of the page processing
     */
    public <T> T navigateToUrl(String url, Set<String> resourceTypesToBlock, PageConsumer<T> pageConsumer) {
        log.info("Navigating to URL: {}", url);
        BrowserContext context = null;
        Page page = null;

        try {
            // Create a new context with synchronized access
            context = createContext();
            if (context == null) {
                throw new RuntimeException("Failed to create browser context");
            }

            // Create a new page
            page = context.newPage();
            if (page == null) {
                throw new RuntimeException("Failed to create page");
            }

            final Page finalPage = page; // For use in lambda expressions

            // Block unnecessary resources to improve performance
            if (resourceTypesToBlock != null && !resourceTypesToBlock.isEmpty()) {
                page.route("**/*", route -> {
                    try {
                        String resourceType = route.request().resourceType();
                        if (resourceTypesToBlock.contains(resourceType)) {
                            route.abort();
                        } else {
                            route.resume();
                        }
                    } catch (Exception e) {
                        log.warn("Error in route handler: {}", e.getMessage());
                        try {
                            route.resume();
                        } catch (Exception ignored) {
                            // Ignore if we can't continue
                        }
                    }
                });
            }

            // Add random delays to mimic human behavior
            addRandomDelays(page);

            try {
                // Navigate to the URL with timeout
                log.info("Navigating to URL with timeout: {}", url);
                page.navigate(url, new Page.NavigateOptions().setTimeout(60000));

                // Wait for the page to load with multiple states
                log.info("Waiting for page load state: DOMCONTENTLOADED");
                page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(60000));

                log.info("Waiting for page load state: NETWORKIDLE");
                page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(60000));

                // Try to detect and handle Cloudflare or other protection
                if (isProtectionDetected(page)) {
                    log.info("Protection detected, waiting for it to clear...");
                    handleProtection(page);
                }

                // Add random scrolling to mimic human behavior
                log.info("Simulating human behavior");
                simulateHumanBehavior(page);

                // Try alternative selectors if the main ones don't work
                log.info("Checking for alternative selectors");
                tryAlternativeSelectors(page);

                // Process the page
                log.info("Processing page content");
                T result = pageConsumer.apply(page);
                log.info("Page processing completed successfully");
                return result;
            } catch (Exception e) {
                log.error("Error during page navigation and processing: {}", e.getMessage());
                // Take screenshot of the error state if possible
                try {
                    String errorScreenshotPath = "error-screenshot-" + System.currentTimeMillis() + ".png";
                    page.screenshot(new Page.ScreenshotOptions().setPath(Path.of(errorScreenshotPath)).setFullPage(true));
                    log.info("Error screenshot saved to {}", errorScreenshotPath);
                } catch (Exception screenshotError) {
                    log.error("Failed to take error screenshot: {}", screenshotError.getMessage());
                }
                throw e;
            }
        } catch (Exception e) {
            log.error("Error navigating to URL {}: {}", url, e.getMessage(), e);
            throw new RuntimeException("Failed to navigate to URL: " + url, e);
        } finally {
            // Clean up resources
            try {
                if (page != null) {
                    try {
                        page.close();
                        log.debug("Page closed successfully");
                    } catch (Exception e) {
                        log.warn("Error closing page: {}", e.getMessage());
                    }
                }

                if (context != null) {
                    try {
                        context.close();
                        log.debug("Browser context closed successfully");
                    } catch (Exception e) {
                        log.warn("Error closing browser context: {}", e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.warn("Error in cleanup: {}", e.getMessage());
            }
        }
    }

    /**
     * Add random delays to page navigation events
     * @param page Playwright page
     */
    private void addRandomDelays(Page page) {
        page.onRequest(request -> {
            try {
                // Add random delay between 100-300ms
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 300));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Simulate human behavior on the page
     * @param page Playwright page
     */
    private void simulateHumanBehavior(Page page) {
        try {
            // Random scrolling
            int scrollCount = ThreadLocalRandom.current().nextInt(3, 8);
            for (int i = 0; i < scrollCount; i++) {
                int scrollY = ThreadLocalRandom.current().nextInt(300, 800);
                page.evaluate("window.scrollBy(0,  + scrollY  )");
                Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1500));
            }

            // Random mouse movements (if not in headless mode)
            if (!headless) {
                int moveCount = ThreadLocalRandom.current().nextInt(3, 8);
                for (int i = 0; i < moveCount; i++) {
                    int x = ThreadLocalRandom.current().nextInt(100, 1000);
                    int y = ThreadLocalRandom.current().nextInt(100, 800);
                    page.mouse().move(x, y);
                    Thread.sleep(ThreadLocalRandom.current().nextInt(200, 800));
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.warn("Error simulating human behavior: {}", e.getMessage());
        }
    }

    /**
     * Get a random user agent from the list
     * @return Random user agent string
     */
    private String getRandomUserAgent() {
        if (userAgentRotationEnabled) {
            int index = ThreadLocalRandom.current().nextInt(USER_AGENTS.size());
            return USER_AGENTS.get(index);
        } else {
            return USER_AGENTS.get(0);
        }
    }

    /**
     * Take a screenshot of the page
     * @param page Playwright page
     * @param path Path to save the screenshot
     */
    public void takeScreenshot(Page page, Path path) {
        try {
            page.screenshot(new Page.ScreenshotOptions().setPath(path).setFullPage(true));
            log.info("Screenshot saved to {}", path);
        } catch (Exception e) {
            log.error("Failed to take screenshot: {}", e.getMessage(), e);
        }
    }

    /**
     * Close the browser and playwright instance
     */
    @PreDestroy
    public void close() {
        synchronized (browserLock) {
            log.info("Closing Playwright resources");
            try {
                if (browser != null) {
                    try {
                        browser.close();
                        log.info("Browser closed successfully");
                    } catch (Exception e) {
                        log.warn("Error closing browser: {}", e.getMessage());
                    } finally {
                        browser = null;
                    }
                }

                if (playwright != null) {
                    try {
                        playwright.close();
                        log.info("Playwright closed successfully");
                    } catch (Exception e) {
                        log.warn("Error closing Playwright: {}", e.getMessage());
                    } finally {
                        playwright = null;
                    }
                }
            } catch (Exception e) {
                log.error("Error closing Playwright resources: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Check if the page has protection mechanisms like Cloudflare
     * @param page Playwright page
     * @return True if protection is detected
     */
    private boolean isProtectionDetected(Page page) {
        try {
            // Check for Cloudflare protection
            boolean hasCloudflare = page.content().contains("cloudflare") ||
                                   page.content().contains("DDoS protection") ||
                                   page.content().contains("security check");

            // Check for CAPTCHA
            boolean hasCaptcha = page.content().contains("captcha") ||
                               page.content().contains("CAPTCHA") ||
                               page.content().contains("robot") ||
                               page.content().contains("human verification");

            return hasCloudflare || hasCaptcha;
        } catch (Exception e) {
            log.warn("Error checking for protection: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Handle protection mechanisms like Cloudflare
     * @param page Playwright page
     */
    private void handleProtection(Page page) {
        try {
            // Wait for protection to clear (usually takes some time)
            log.info("Waiting for protection to clear...");
            page.waitForTimeout(10000); // Wait 10 seconds

            // Try to find and click on any "I'm a human" or "Continue" buttons
            for (String selector : Arrays.asList(
                    "button[type=submit]",
                    "input[type=submit]",
                    "a.button",
                    "#challenge-running",
                    "#challenge-form")) {
                try {
                    if (page.isVisible(selector)) {
                        log.info("Found protection element: {}", selector);
                        page.click(selector);
                        page.waitForTimeout(5000);
                    }
                } catch (Exception e) {
                    log.debug("No element found for selector: {}", selector);
                }
            }

            // Wait for navigation to complete after handling protection
            page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(30000));
        } catch (Exception e) {
            log.warn("Error handling protection: {}", e.getMessage());
        }
    }

    /**
     * Try alternative selectors if the main ones don't work
     * @param page Playwright page
     */
    private void tryAlternativeSelectors(Page page) {
        // List of possible product item selectors for Shopee
        List<String> shopeeSelectors = Arrays.asList(
                ".shopee-search-item-result__item",
                ".col-xs-2-4",
                "[data-sqe='item']",
                ".shopee-search-item-result__items .shopee-search-item-result__item",
                ".row .col-xs-2-4"
        );

        // Try each selector
        for (String selector : shopeeSelectors) {
            try {
                log.info("Trying selector: {}", selector);
                boolean hasElements = page.querySelector(selector) != null;
                if (hasElements) {
                    log.info("Found elements with selector: {}", selector);
                    // If found elements, no need to try other selectors
                    break;
                }
            } catch (Exception e) {
                log.debug("Error with selector {}: {}", selector, e.getMessage());
            }
        }
    }

    /**
     * Functional interface for processing a page
     * @param <T> Return type
     */
    @FunctionalInterface
    public interface PageConsumer<T> {
        T apply(Page page);
    }
}
