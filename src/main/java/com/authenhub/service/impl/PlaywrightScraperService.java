package com.authenhub.service.impl;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Geolocation;
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
                                // Disable automation flags to avoid detection
                                "--disable-blink-features=AutomationControlled",
                                // Standard performance options
                                "--disable-gpu",
                                "--disable-dev-shm-usage",
                                "--disable-setuid-sandbox",
                                "--no-sandbox",
                                "--disable-accelerated-2d-canvas",
                                // Disable browser features that might reveal automation
                                "--disable-notifications",
                                "--disable-extensions",
                                "--disable-infobars",
                                // Security and performance settings
                                "--disable-web-security",
                                "--disable-features=IsolateOrigins,site-per-process",
                                "--disable-popup-blocking",
                                // Window size for more realistic browsing
                                "--window-size=1920,1080",
                                // Disable automation-specific features
                                "--disable-automation",
                                // Set language for Vietnamese sites
                                "--lang=vi-VN,vi",
                                // User agent that matches a real browser
                                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36"
                        ))
                        .setTimeout(180000); // Increase timeout to 3 minutes for slower connections

                // Add proxy if enabled
                if (proxyEnabled && !proxyHost.isEmpty() && proxyPort > 0) {
                    Proxy proxy = new Proxy(proxyHost + ":" + proxyPort);
                    if (!proxyUsername.isEmpty() && !proxyPassword.isEmpty()) {
                        proxy.setUsername(proxyUsername);
                        proxy.setPassword(proxyPassword);
                    }
                    launchOptions.setProxy(proxy);
                }

                // Use persistent context for better anti-bot evasion
                String userDataDir = "./browser-data";
                log.info("Using persistent browser data directory: {}", userDataDir);

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

                // Get a realistic user agent for Vietnamese users
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36";
                if (userAgentRotationEnabled) {
                    userAgent = getRandomUserAgent();
                }
                log.info("Using user agent: {}", userAgent);

                Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                        .setJavaScriptEnabled(true)
                        .setUserAgent(userAgent)
                        .setViewportSize(1920, 1080)
                        .setDeviceScaleFactor(1.0)
                        .setHasTouch(false)
                        .setIsMobile(false)
                        // Use Vietnamese locale for better compatibility with Vietnamese sites
                        .setLocale("vi-VN")
                        .setTimezoneId("Asia/Ho_Chi_Minh")
                        .setBypassCSP(true) // Bypass Content Security Policy
                        .setIgnoreHTTPSErrors(true); // Ignore HTTPS errors

                // Set geolocation to Vietnam for better compatibility
                Map<String, Object> geolocation = new HashMap<>();
                Geolocation geo = new Geolocation(10.8231, 106.6297);
                geolocation.put("latitude", 10.8231); // Ho Chi Minh City coordinates
                geolocation.put("longitude", 106.6297);
                geolocation.put("accuracy", 100);
                contextOptions.setGeolocation(geo);

                // Set additional headers to mimic a real browser
                Map<String, String> extraHeaders = new HashMap<>();
                extraHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
                extraHeaders.put("Accept-Language", "vi-VN,vi;q=0.9,en-US;q=0.8,en;q=0.7");
                extraHeaders.put("Accept-Encoding", "gzip, deflate, br");
                extraHeaders.put("Connection", "keep-alive");
                extraHeaders.put("Upgrade-Insecure-Requests", "1");
                extraHeaders.put("Sec-Fetch-Dest", "document");
                extraHeaders.put("Sec-Fetch-Mode", "navigate");
                extraHeaders.put("Sec-Fetch-Site", "none");
                extraHeaders.put("Sec-Fetch-User", "?1");
                extraHeaders.put("Cache-Control", "max-age=0");
                extraHeaders.put("sec-ch-ua", "\"Google Chrome\";v=\"133\", \"Not-A.Brand\";v=\"8\", \"Chromium\";v=\"133\"");
                extraHeaders.put("sec-ch-ua-mobile", "?0");
                extraHeaders.put("sec-ch-ua-platform", "\"Windows\"");

                contextOptions.setExtraHTTPHeaders(extraHeaders);

                log.info("Creating new browser context with enhanced anti-bot settings");
                BrowserContext context = browser.newContext(contextOptions);

                // Add scripts to modify browser fingerprinting to avoid bot detection
                context.addInitScript("Object.defineProperty(navigator, 'webdriver', { get: () => false });");
                context.addInitScript("window.navigator.chrome = { runtime: {} };");
                context.addInitScript("Object.defineProperty(navigator, 'plugins', { get: () => [1, 2, 3, 4, 5] });");
                context.addInitScript("Object.defineProperty(navigator, 'languages', { get: () => ['vi-VN', 'vi', 'en-US', 'en'] });");

                // Add more advanced evasion techniques
                context.addInitScript(
                    "const originalQuery = window.navigator.permissions.query;" +
                    "window.navigator.permissions.query = (parameters) => (" +
                    "  parameters.name === 'notifications' ?" +
                    "  Promise.resolve({ state: Notification.permission }) :" +
                    "  originalQuery(parameters)" +
                    ");"
                );

                // Mask WebGL fingerprinting
                context.addInitScript(
                    "const getParameter = WebGLRenderingContext.prototype.getParameter;" +
                    "WebGLRenderingContext.prototype.getParameter = function(parameter) {" +
                    "  if (parameter === 37445) { return 'Intel Inc.'; }" +
                    "  if (parameter === 37446) { return 'Intel Iris OpenGL Engine'; }" +
                    "  return getParameter.apply(this, arguments);" +
                    "};"
                );

                return context;
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
                // Add a cookie consent handler before navigation
                page.route("**/*", route -> {
                    if (route.request().url().contains("consent") ||
                        route.request().url().contains("cookie") ||
                        route.request().url().contains("gdpr")) {
                        log.info("Detected potential cookie/consent request: {}", route.request().url());
                    }
                    route.resume();
                });

                // Navigate to the URL with increased timeout
                log.info("Navigating to URL with timeout: {}", url);
                page.navigate(url, new Page.NavigateOptions().setTimeout(120000));

                // Wait for the page to load with multiple states
                log.info("Waiting for page load state: DOMCONTENTLOADED");
                page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(120000));

                // Take a screenshot after initial load for debugging
                String initialScreenshotPath = "initial-load-" + System.currentTimeMillis() + ".png";
                page.screenshot(new Page.ScreenshotOptions().setPath(Path.of(initialScreenshotPath)).setFullPage(true));
                log.info("Initial page load screenshot saved to {}", initialScreenshotPath);

                // Check if page has any content
                boolean hasContent = (boolean) page.evaluate("() => document.body.innerText.length > 0");
                if (!hasContent) {
                    log.warn("Page appears to be blank after initial load");

                    // Try to bypass potential JavaScript challenge
                    log.info("Attempting to bypass JavaScript challenge...");
                    page.evaluate("() => { window.scrollTo(0, document.body.scrollHeight / 2); }");
                    page.waitForTimeout(3000);

                    // Try to click any "Continue" or "I'm not a robot" buttons
                    for (String selector : Arrays.asList(
                            "button", "input[type=submit]", "a.button", ".btn", "[type=button]")) {
                        try {
                            if (page.isVisible(selector)) {
                                log.info("Found and clicking element: {}", selector);
                                page.click(selector);
                                page.waitForTimeout(5000);
                            }
                        } catch (Exception e) {
                            log.debug("No clickable element found for selector: {}", selector);
                        }
                    }
                }

                log.info("Waiting for page load state: NETWORKIDLE");
                page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(120000));

                // Try to detect and handle Cloudflare or other protection
                if (isProtectionDetected(page)) {
                    log.info("Protection detected, waiting for it to clear...");
                    handleProtection(page);
                }

                // Add random scrolling to mimic human behavior
                log.info("Simulating human behavior");
                simulateHumanBehavior(page);

                // Wait a bit after scrolling
                page.waitForTimeout(2000);

                // Take another screenshot after scrolling
                String afterScrollScreenshotPath = "after-scroll-" + System.currentTimeMillis() + ".png";
                page.screenshot(new Page.ScreenshotOptions().setPath(Path.of(afterScrollScreenshotPath)).setFullPage(true));
                log.info("After scrolling screenshot saved to {}", afterScrollScreenshotPath);

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
     * Check if the page has protection mechanisms like Cloudflare or Shopee's anti-bot
     * @param page Playwright page
     * @return True if protection is detected
     */
    private boolean isProtectionDetected(Page page) {
        try {
            // Get page content for analysis
            String content = page.content();

            // Take a screenshot for debugging protection detection
            String protectionCheckScreenshot = "protection-check-" + System.currentTimeMillis() + ".png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Path.of(protectionCheckScreenshot)).setFullPage(true));

            // Check for Cloudflare protection
            boolean hasCloudflare = content.contains("cloudflare") ||
                                   content.contains("DDoS protection") ||
                                   content.contains("security check") ||
                                   content.contains("checking your browser");

            // Check for CAPTCHA
            boolean hasCaptcha = content.contains("captcha") ||
                               content.contains("CAPTCHA") ||
                               content.contains("robot") ||
                               content.contains("human verification") ||
                               content.contains("verify you are human");

            // Check for Shopee specific protection
            boolean hasShopeeProtection = content.contains("shopee-captcha") ||
                                        content.contains("_gcaptcha") ||
                                        content.contains("security verification") ||
                                        content.contains("xác minh") ||
                                        content.contains("xác thực") ||
                                        content.contains("kiểm tra bảo mật");

            // Check for blank page (potential JavaScript challenge)
            boolean isBlankPage = (boolean) page.evaluate("() => document.body.innerText.trim().length < 50");

            // Check for specific Shopee elements that should be present
            boolean missingShopeeElements = !(boolean) page.evaluate(
                "() => !!document.querySelector('.shopee-search-item-result__item, .col-xs-2-4, [data-sqe=\"item\"]')");

            if (hasCloudflare || hasCaptcha || hasShopeeProtection || (isBlankPage && missingShopeeElements)) {
                log.info("Protection detected: Cloudflare={}, CAPTCHA={}, Shopee={}, BlankPage={}, MissingElements={}",
                    hasCloudflare, hasCaptcha, hasShopeeProtection, isBlankPage, missingShopeeElements);
                return true;
            }

            return false;
        } catch (Exception e) {
            log.warn("Error checking for protection: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Handle protection mechanisms like Cloudflare or Shopee's anti-bot
     * @param page Playwright page
     */
    private void handleProtection(Page page) {
        try {
            // Take a screenshot before handling protection
            String beforeProtectionScreenshot = "before-protection-" + System.currentTimeMillis() + ".png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Path.of(beforeProtectionScreenshot)).setFullPage(true));
            log.info("Protection screenshot saved to {}", beforeProtectionScreenshot);

            // Wait for protection to clear (usually takes some time)
            log.info("Waiting for protection to clear...");
            page.waitForTimeout(15000); // Wait 15 seconds

            // Try to bypass JavaScript challenges by scrolling
            log.info("Attempting to scroll to bypass JavaScript challenges");
            page.evaluate("() => { window.scrollTo(0, 100); }");
            page.waitForTimeout(2000);
            page.evaluate("() => { window.scrollTo(0, 300); }");
            page.waitForTimeout(2000);

            // Try to find and click on any "I'm a human" or "Continue" buttons
            for (String selector : Arrays.asList(
                    "button[type=submit]",
                    "input[type=submit]",
                    "a.button",
                    "#challenge-running",
                    "#challenge-form",
                    ".shopee-captcha",
                    ".btn-captcha",
                    "button:not([disabled])",
                    "[type=button]:not([disabled])",
                    ".btn:not([disabled])")) {
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

            // Try to handle Shopee specific challenges
            try {
                // Check for slider captcha
                if (page.isVisible(".shopee-captcha__slider")) {
                    log.info("Detected Shopee slider captcha, attempting to solve");
                    // Simulate slider drag (this is a basic implementation)
                    page.mouse().move(
                            (Double) page.evaluate("() => document.querySelector('.shopee-captcha__slider').getBoundingClientRect().x + 10"),
                            (Double) page.evaluate("() => document.querySelector('.shopee-captcha__slider').getBoundingClientRect().y + 10")
                    );
                    page.mouse().down();
                    page.waitForTimeout(500);


                    page.waitForTimeout(500);
                    page.mouse().up();
                    page.waitForTimeout(3000);
                }
            } catch (Exception e) {
                log.debug("Error handling Shopee slider: {}", e.getMessage());
            }

            // Wait for navigation to complete after handling protection
            page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(30000));

            // Take a screenshot after handling protection
            String afterProtectionScreenshot = "after-protection-" + System.currentTimeMillis() + ".png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Path.of(afterProtectionScreenshot)).setFullPage(true));
            log.info("After protection handling screenshot saved to {}", afterProtectionScreenshot);
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
                // Standard Shopee selectors
                ".shopee-search-item-result__item",
                ".col-xs-2-4",
                "[data-sqe='item']",
                ".shopee-search-item-result__items .shopee-search-item-result__item",
                ".row .col-xs-2-4",
                // New alternative selectors for Shopee
                ".shopee-search-result-view [data-sqe]",
                ".shopee-search-item-result__items > div",
                ".shopee-search-item-result__item a",
                ".shopee-search-result-view a[href*='/product/']",
                // Generic selectors that might work
                "div[role='listitem']",
                "div[data-testid='item']",
                "a[href*='/product/']",
                // Fallback to any link that might be a product
                "a[href]:not([href='#'])"
        );

//        // Try each selector with a longer timeout
//        for (String selector : shopeeSelectors) {
//            try {
//                log.info("Trying selector: {}", selector);
//
//                // Try to wait for the selector with a timeout
//                try {
//                    page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(10000));
//                    log.info("Successfully waited for selector: {}", selector);
//                } catch (Exception e) {
//                    log.debug("Timeout waiting for selector {}: {}", selector, e.getMessage());
//                }
//
//                // Check if elements exist
//                boolean hasElements = page.querySelector(selector) != null;
//                if (hasElements) {
//                    log.info("Found elements with selector: {}", selector);
//
//                    // Count elements
//                    int count = (int) page.evaluate("selector => document.querySelectorAll(selector).length", selector);
//                    log.info("Found {} elements with selector: {}", count, selector);
//
//                    // Take a screenshot highlighting the elements
//                    try {
//                        // Add a red border to the elements for debugging
//                        page.evaluate("selector => { " +
//                            "const elements = document.querySelectorAll(selector); " +
//                            "for (const el of elements) { " +
//                            "  el.style.border = '2px solid red'; " +
//                            "} " +
//                        "}", selector);
//
//                        String selectorScreenshotPath = "selector-" + selector.replaceAll("[^a-zA-Z0-9]", "-") + "-" + System.currentTimeMillis() + ".png";
//                        page.screenshot(new Page.ScreenshotOptions().setPath(Path.of(selectorScreenshotPath)).setFullPage(true));
//                        log.info("Selector screenshot saved to {}", selectorScreenshotPath);
//
//                        // Remove the border
//                        page.evaluate("selector => { " +
//                            "const elements = document.querySelectorAll(selector); " +
//                            "for (const el of elements) { " +
//                            "  el.style.border = ''; " +
//                            "} " +
//                        "}", selector);
//                    } catch (Exception e) {
//                        log.debug("Error taking selector screenshot: {}", e.getMessage());
//                    }
//
//                    // If found elements, no need to try other selectors
//                    break;
//                }
//            } catch (Exception e) {
//                log.debug("Error with selector {}: {}", selector, e.getMessage());
//            }
//        }
//
//        // If no selectors worked, try to analyze the page structure
//        try {
//            log.info("Analyzing page structure to find product elements");
//            String pageStructure = (String) page.evaluate("() => {{" +
//                "const links = Array.from(document.querySelectorAll('a[href]'));" +
//                "return links.filter(a => a.href.includes('/product/') || a.href.includes('/-i.')).map(a => a.href).join('\\n');" +
//            "}}");
//
//            if (!pageStructure.isEmpty()) {
//                log.info("Found potential product links through page analysis: {}",
//                    pageStructure.split("\\n").length);
//            } else {
//                log.warn("No product links found through page analysis");
//            }
//        } catch (Exception e) {
//            log.debug("Error analyzing page structure: {}", e.getMessage());
//        }
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
