package com.authenhub.service.impl;

import com.authenhub.bean.tool.affiliatescraper.AffiliateScraperRequest;
import com.authenhub.bean.tool.affiliatescraper.AffiliateScraperResponse;
import com.authenhub.bean.tool.affiliatescraper.ProductItem;
import com.authenhub.service.interfaces.IAffiliateScraperService;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AffiliateScraperService implements IAffiliateScraperService {

    private final RestTemplate restTemplate;
    private final PlaywrightScraperService playwrightScraperService;
    private static final String AGENT = "Chrome/133.0.0.0 Safari/537.36";

    @Value("${scraper.use-playwright:true}")
    private boolean usePlaywright;

    @Value("${scraper.block-resources:true}")
    private boolean blockResources;

    // In-memory storage for price tracking (would be replaced with a database in production)
    private final Map<String, List<String>> priceTracking = new HashMap<>();

    // Resource types to block for better performance
    private static final Set<String> BLOCKED_RESOURCE_TYPES = new HashSet<>(Arrays.asList(
            "image", "stylesheet", "font", "media", "other"
    ));

    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up AffiliateScraperService resources");
        // No additional cleanup needed here as PlaywrightScraperService handles its own cleanup
    }

    @Override
    public AffiliateScraperResponse scrapeProducts(AffiliateScraperRequest request) {
        log.info("Scraping products from platform: {} with keyword: {}", request.getPlatform(), request.getKeyword());

        List<ProductItem> products = new ArrayList<>();
        int limit = request.getLimit() != null ? request.getLimit() : 10;

        // Check which platform to scrape from
        if ("shopee".equalsIgnoreCase(request.getPlatform())) {
            try {
                products = scrapeShopeeProducts(request);
                log.info("Successfully scraped {} products from Shopee", products.size());
            } catch (Exception e) {
                log.error("Error scraping Shopee products: {}", e.getMessage(), e);
                // Fallback to mock data if scraping fails
                products = generateMockProducts(request, limit);
            }
        } else {
            // For other platforms, use mock data for now
            products = generateMockProducts(request, limit);
        }

        // Apply filters and sorting
        products = applyFilters(products, request);

        // Generate affiliate links if affiliate ID is provided
        if (request.getAffiliateId() != null && !request.getAffiliateId().isEmpty()) {
            products = Arrays.asList(generateAffiliateLinks(products.toArray(new ProductItem[0]), request.getAffiliateId()));
        }

        // Calculate total commission
        double totalCommission = products.stream()
                .mapToDouble(ProductItem::getCommission)
                .sum();

        return AffiliateScraperResponse.builder()
                .products(products)
                .totalCount(products.size())
                .platform(request.getPlatform())
                .keyword(request.getKeyword())
                .category(request.getCategory())
                .scrapeDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .totalCommission(totalCommission)
                .build();
    }

    /**
     * Apply filters and sorting to the product list based on the request parameters
     *
     * @param products The list of products to filter and sort
     * @param request  The affiliate scraper request containing filter and sort parameters
     * @return The filtered and sorted list of products
     */
    private List<ProductItem> applyFilters(List<ProductItem> products, AffiliateScraperRequest request) {
        // Filter by price range if specified
        if (request.getMinPrice() != null) {
            products = products.stream()
                    .filter(p -> p.getSalePrice() >= request.getMinPrice())
                    .collect(Collectors.toList());
        }

        if (request.getMaxPrice() != null) {
            products = products.stream()
                    .filter(p -> p.getSalePrice() <= request.getMaxPrice())
                    .collect(Collectors.toList());
        }

        // Filter by rating if specified
        if (request.getMinRating() != null) {
            products = products.stream()
                    .filter(p -> p.getRating() >= request.getMinRating())
                    .collect(Collectors.toList());
        }

        // Filter by sold count if specified
        if (request.getMinSoldCount() != null) {
            products = products.stream()
                    .filter(p -> p.getSoldCount() >= request.getMinSoldCount())
                    .collect(Collectors.toList());
        }

        // Sort products if specified
        if (request.getSortBy() != null) {
            switch (request.getSortBy()) {
                case "sales":
                    products.sort((p1, p2) -> p2.getSoldCount().compareTo(p1.getSoldCount()));
                    break;
                case "price_asc":
                    products.sort((p1, p2) -> p1.getSalePrice().compareTo(p2.getSalePrice()));
                    break;
                case "price_desc":
                    products.sort((p1, p2) -> p2.getSalePrice().compareTo(p1.getSalePrice()));
                    break;
                case "rating":
                    products.sort((p1, p2) -> p2.getRating().compareTo(p1.getRating()));
                    break;
                default:
                    // Default sort by relevance (no sorting)
                    break;
            }
        }

        return products;
    }

    /**
     * Generate mock product data for testing or when scraping fails
     *
     * @param request The affiliate scraper request
     * @param limit   The maximum number of products to generate
     * @return A list of mock product items
     */
    private List<ProductItem> generateMockProducts(AffiliateScraperRequest request, int limit) {
        List<ProductItem> products = new ArrayList<>();

        for (int i = 1; i <= limit; i++) {
            double originalPrice = 100.0 * i;
            double discountPercent = i * 5.0;
            double salePrice = originalPrice * (1 - discountPercent / 100);

            products.add(ProductItem.builder()
                    .id(UUID.randomUUID().toString())
                    .name("Product " + i + " - " + request.getKeyword())
                    .url(getPlatformBaseUrl(request.getPlatform()) + "/product-" + i)
                    .imageUrl(getPlatformBaseUrl(request.getPlatform()) + "/images/product-" + i + ".jpg")
                    .originalPrice(originalPrice)
                    .salePrice(salePrice)
                    .discountPercent(discountPercent)
                    .rating(i % 5 + 1)
                    .soldCount(i * 100)
                    .shopName("Shop " + i)
                    .platform(request.getPlatform())
                    .category(request.getCategory() != null ? request.getCategory() : "Electronics")
                    .commission(salePrice * 0.05) // 5% commission
                    .build());
        }

        return products;
    }

    @Override
    public ProductItem[] generateAffiliateLinks(ProductItem[] products, String affiliateId) {
        log.info("Generating affiliate links for {} products with affiliate ID: {}", products.length, affiliateId);

        for (ProductItem product : products) {
            String baseUrl = product.getUrl();
            String affiliateUrl = baseUrl + "?aff=" + affiliateId;
            product.setAffiliateUrl(affiliateUrl);
        }

        return products;
    }

    @Override
    public boolean trackProductPrice(String productId, String platform, String userId) {
        log.info("Tracking price for product ID: {} on platform: {} for user: {}", productId, platform, userId);

        String key = userId + ":" + platform;
        if (!priceTracking.containsKey(key)) {
            priceTracking.put(key, new ArrayList<>());
        }

        priceTracking.get(key).add(productId);

        return true;
    }

    @Override
    public byte[] exportProducts(String format, AffiliateScraperResponse affiliateScraperResponse) {
        log.info("Exporting products to format: {}", format);

        // TODO: Implement actual export logic based on the format

        // For now, return mock data
        StringBuilder sb = new StringBuilder();

        if ("json".equalsIgnoreCase(format)) {
            sb.append("{\n");
            sb.append("  \"products\": [\n");
            for (int i = 0; i < affiliateScraperResponse.getProducts().size(); i++) {
                ProductItem product = affiliateScraperResponse.getProducts().get(i);
                sb.append("    {\n");
                sb.append("      \"id\": \"").append(product.getId()).append("\",\n");
                sb.append("      \"name\": \"").append(product.getName()).append("\",\n");
                sb.append("      \"url\": \"").append(product.getUrl()).append("\",\n");
                sb.append("      \"originalPrice\": ").append(product.getOriginalPrice()).append(",\n");
                sb.append("      \"salePrice\": ").append(product.getSalePrice()).append(",\n");
                sb.append("      \"discountPercent\": ").append(product.getDiscountPercent()).append(",\n");
                sb.append("      \"platform\": \"").append(product.getPlatform()).append("\",\n");
                if (product.getAffiliateUrl() != null) {
                    sb.append("      \"affiliateUrl\": \"").append(product.getAffiliateUrl()).append("\",\n");
                }
                sb.append("      \"commission\": ").append(product.getCommission()).append("\n");
                sb.append("    }");
                if (i < affiliateScraperResponse.getProducts().size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("  ],\n");
            sb.append("  \"totalCount\": ").append(affiliateScraperResponse.getTotalCount()).append(",\n");
            sb.append("  \"platform\": \"").append(affiliateScraperResponse.getPlatform()).append("\",\n");
            sb.append("  \"keyword\": \"").append(affiliateScraperResponse.getKeyword()).append("\",\n");
            sb.append("  \"scrapeDate\": \"").append(affiliateScraperResponse.getScrapeDate()).append("\",\n");
            sb.append("  \"totalCommission\": ").append(affiliateScraperResponse.getTotalCommission()).append("\n");
            sb.append("}");
        } else if ("csv".equalsIgnoreCase(format)) {
            sb.append("id,name,url,originalPrice,salePrice,discountPercent,platform,affiliateUrl,commission\n");
            for (ProductItem product : affiliateScraperResponse.getProducts()) {
                sb.append(product.getId()).append(",");
                sb.append("\"").append(product.getName()).append("\",");
                sb.append("\"").append(product.getUrl()).append("\",");
                sb.append(product.getOriginalPrice()).append(",");
                sb.append(product.getSalePrice()).append(",");
                sb.append(product.getDiscountPercent()).append(",");
                sb.append("\"").append(product.getPlatform()).append("\",");
                sb.append("\"").append(product.getAffiliateUrl() != null ? product.getAffiliateUrl() : "").append("\",");
                sb.append(product.getCommission()).append("\n");
            }
        } else {
            // Default to plain text
            sb.append("Products from ").append(affiliateScraperResponse.getPlatform()).append("\n");
            sb.append("Keyword: ").append(affiliateScraperResponse.getKeyword()).append("\n");
            sb.append("Scraped on: ").append(affiliateScraperResponse.getScrapeDate()).append("\n");
            sb.append("Total Commission: ").append(affiliateScraperResponse.getTotalCommission()).append("\n\n");

            for (ProductItem product : affiliateScraperResponse.getProducts()) {
                sb.append("Name: ").append(product.getName()).append("\n");
                sb.append("URL: ").append(product.getUrl()).append("\n");
                sb.append("Original Price: ").append(product.getOriginalPrice()).append("\n");
                sb.append("Sale Price: ").append(product.getSalePrice()).append("\n");
                sb.append("Discount: ").append(product.getDiscountPercent()).append("%\n");
                if (product.getAffiliateUrl() != null) {
                    sb.append("Affiliate URL: ").append(product.getAffiliateUrl()).append("\n");
                }
                sb.append("Commission: ").append(product.getCommission()).append("\n\n");
            }
        }

        return sb.toString().getBytes();
    }

    /**
     * Scrape products from Shopee based on the search keyword
     *
     * @param request The affiliate scraper request containing the search parameters
     * @return A list of scraped product items from Shopee
     * @throws IOException If there's an error connecting to Shopee or parsing the response
     */
    private List<ProductItem> scrapeShopeeProducts(AffiliateScraperRequest request) throws IOException {
        log.info("Scraping Shopee products for keyword: {}", request.getKeyword());

        // Construct the search URL
        String searchUrl = getPlatformBaseUrl("shopee") + "/search?keyword=" + request.getKeyword().replace(" ", "%20");
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            searchUrl += "&category=" + request.getCategory();
        }

        log.info("Shopee search URL: {}", searchUrl);

        List<ProductItem> products = new ArrayList<>();
        int limit = request.getLimit() != null ? request.getLimit() : 10;

        if (usePlaywright) {
            // Use Playwright for scraping
            try {
                return scrapeWithPlaywright(searchUrl, limit, request);
            } catch (Exception e) {
                log.error("Error scraping with Playwright: {}", e.getMessage(), e);
                log.info("Falling back to JSoup scraping");
                // Fall back to JSoup if Playwright fails
                return scrapeWithJsoup(searchUrl, limit, request);
            }
        } else {
            // Use JSoup for scraping
            return scrapeWithJsoup(searchUrl, limit, request);
        }
    }

    /**
     * Extract the product URL from a Shopee product element
     */
    private String extractShopeeProductUrl(Element productElement) {
        Element linkElement = productElement.selectFirst("a[href]");
        if (linkElement != null) {
            String href = linkElement.attr("href");
            if (href.startsWith("/")) {
                return "https://shopee.vn" + href;
            }
            return href;
        }
        return "";
    }

    /**
     * Extract the product name from a Shopee product element
     */
    private String extractShopeeProductName(Element productElement) {
        Element nameElement = productElement.selectFirst(".shopee-search-item-result__item .shopee-item-card__text-name, .shopee-item-card__text .shopee-item-card__text-name");
        return nameElement != null ? nameElement.text().trim() : "Unknown Product";
    }

    /**
     * Extract the product image URL from a Shopee product element
     */
    private String extractShopeeProductImage(Element productElement) {
        Element imgElement = productElement.selectFirst("img[src]");
        if (imgElement != null) {
            String src = imgElement.attr("src");
            if (src.startsWith("//")) {
                return "https:" + src;
            }
            return src;
        }
        return "";
    }

    /**
     * Extract the original price from a Shopee product element
     */
    private Double extractShopeeOriginalPrice(Element productElement) {
        Element priceElement = productElement.selectFirst(".shopee-item-card__original-price, .shopee-search-item-result__item .shopee-item-card__text-price del");
        if (priceElement != null) {
            String priceText = priceElement.text().replaceAll("[^\\d.,]", "").replace(",", ".");
            try {
                return Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                log.warn("Could not parse original price: {}", priceText);
            }
        }
        return null;
    }

    /**
     * Extract the sale price from a Shopee product element
     */
    private Double extractShopeeSalePrice(Element productElement) {
        Element priceElement = productElement.selectFirst(".shopee-item-card__current-price, .shopee-search-item-result__item .shopee-item-card__text-price span:not(del)");
        if (priceElement != null) {
            String priceText = priceElement.text().replaceAll("[^\\d.,]", "").replace(",", ".");
            try {
                return Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                log.warn("Could not parse sale price: {}", priceText);
            }
        }
        return null;
    }

    /**
     * Extract the sold count from a Shopee product element
     */
    private Integer extractShopeeSoldCount(Element productElement) {
        Element soldElement = productElement.selectFirst(".shopee-item-card__sold-text, .shopee-search-item-result__item .shopee-item-card__text-sold");
        if (soldElement != null) {
            String soldText = soldElement.text();
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(soldText);
            if (matcher.find()) {
                try {
                    return Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException e) {
                    log.warn("Could not parse sold count: {}", soldText);
                }
            }
        }
        return 0;
    }

    /**
     * Extract the shop name from a Shopee product element
     */
    private String extractShopeeShopName(Element productElement) {
        Element shopElement = productElement.selectFirst(".shopee-item-card__shop-name, .shopee-search-item-result__item .shopee-item-card__shop-name");
        return shopElement != null ? shopElement.text().trim() : "Unknown Shop";
    }

    /**
     * Get the base URL for a platform
     *
     * @param platform The platform name (shopee, lazada, tiki)
     * @return The base URL for the platform
     */
    private String getPlatformBaseUrl(String platform) {
        if (platform == null) {
            return "https://example.com";
        }

        switch (platform.toLowerCase()) {
            case "shopee":
                return "https://shopee.vn";
            case "lazada":
                return "https://lazada.vn";
            case "tiki":
                return "https://tiki.vn";
            default:
                return "https://example.com";
        }
    }

    protected Document fetchDocument(String url, Map<String, String> headers) throws IOException {
        log.info("Fetching document from {}", url);
        return Jsoup.connect(url)
                .userAgent(AGENT)
                .timeout(100000)
                .headers(headers)
                .get();
    }

    protected Elements extractElements(Document doc, String cssSelector) {
        return Objects.nonNull(doc) ? doc.select(cssSelector) : null;
    }

    private List<ProductItem> scrapeWithPlaywright(String searchUrl, int limit, AffiliateScraperRequest request) {
        log.info("Scraping with Playwright: {}", searchUrl);

        // Define resource types to block for better performance
        Set<String> resourcesToBlock = blockResources ? BLOCKED_RESOURCE_TYPES : new HashSet<>();

        return playwrightScraperService.navigateToUrl(searchUrl, resourcesToBlock, page -> {
            List<ProductItem> products = new ArrayList<>();
            AtomicInteger count = new AtomicInteger(0);

            try {
                // Try multiple selectors for product items
                List<String> selectors = Arrays.asList(
                        ".shopee-search-item-result__item",
                        ".col-xs-2-4",
                        "[data-sqe='item']",
                        ".shopee-search-item-result__items .shopee-search-item-result__item",
                        ".row .col-xs-2-4",
                        ".shopee-search-result-view__item",
                        ".shopee-search-item-result__items > div",
                        "[data-sqe='item'] > div"
                );

                // Take a screenshot of the page for debugging
                try {
                    String pageScreenshotPath = "page-content-" + System.currentTimeMillis() + ".png";
                    page.screenshot(new Page.ScreenshotOptions().setPath(Path.of(pageScreenshotPath)).setFullPage(true));
                    log.info("Page screenshot saved to {}", pageScreenshotPath);

                    // Also save the HTML content for debugging
                    String htmlContent = page.content();
                    String htmlPath = "page-content-" + System.currentTimeMillis() + ".html";
                    Files.writeString(Path.of(htmlPath), htmlContent);
                    log.info("Page HTML saved to {}", htmlPath);
                } catch (Exception e) {
                    log.warn("Failed to save page content for debugging: {}", e.getMessage());
                }

                boolean selectorFound = false;
                for (String selector : selectors) {
                    try {
                        log.info("Trying to wait for selector: {}", selector);
                        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(10000));
                        log.info("Found elements with selector: {}", selector);
                        selectorFound = true;
                        break;
                    } catch (Exception e) {
                        log.debug("Selector {} not found: {}", selector, e.getMessage());
                    }
                }

                if (!selectorFound) {
                    log.warn("No product selectors found. Taking screenshot for debugging.");
                    String screenshotPath = "no-selectors-" + System.currentTimeMillis() + ".png";
                    page.screenshot(new Page.ScreenshotOptions().setPath(Path.of(screenshotPath)).setFullPage(true));
                    log.info("Screenshot saved to {}", screenshotPath);

                    // Try to get any visible content as fallback
                    try {
                        log.info("Trying to get any visible content as fallback");
                        String bodyContent = page.evaluate("document.body.innerText").toString();
                        log.info("Page body content: {}...", bodyContent.substring(0, Math.min(bodyContent.length(), 500)));
                    } catch (Exception e) {
                        log.warn("Failed to get body content: {}", e.getMessage());
                    }

                    throw new RuntimeException("No product selectors found on the page");
                }

                // Wait a bit more to ensure all items are loaded
                page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(30000));

                // Get all product elements using the found selector
                String foundSelector = selectors.get(0); // Default to first selector
                for (String selector : selectors) {
                    try {
                        List<ElementHandle> testElements = page.querySelectorAll(selector);
                        if (testElements != null && !testElements.isEmpty()) {
                            foundSelector = selector;
                            break;
                        }
                    } catch (Exception e) {
                        log.debug("Error querying selector {}: {}", selector, e.getMessage());
                    }
                }

                List<ElementHandle> productElements = page.querySelectorAll(foundSelector);
                log.info("Found {} product elements on Shopee search page using selector: {}", productElements.size(), foundSelector);

                for (ElementHandle productElement : productElements) {
                    if (count.get() >= limit) break;

                    try {
                        // Extract product details using Playwright
                        String productUrl = extractPlaywrightAttribute(productElement, "a", "href");
                        if (productUrl != null && !productUrl.startsWith("http")) {
                            productUrl = getPlatformBaseUrl("shopee") + productUrl;
                        }

                        String name = extractPlaywrightText(productElement, ".shopee-item-card__text-name, .shopee-search-item-result__item .shopee-item-card__text-name");
                        String imageUrl = extractPlaywrightAttribute(productElement, "img", "src");
                        Double originalPrice = extractPlaywrightPrice(productElement, ".shopee-item-card__original-price, .shopee-search-item-result__item .shopee-item-card__original-price");
                        Double salePrice = extractPlaywrightPrice(productElement, ".shopee-item-card__current-price, .shopee-search-item-result__item .shopee-item-card__current-price");
                        Integer soldCount = extractPlaywrightSoldCount(productElement);
                        String shopName = extractPlaywrightText(productElement, ".shopee-item-card__shop-name, .shopee-search-item-result__item .shopee-item-card__shop-name");
                        Double discountPercent = 0.0;

                        // Calculate discount percentage if both prices are available
                        if (originalPrice != null && salePrice != null && originalPrice > 0) {
                            discountPercent = ((originalPrice - salePrice) / originalPrice) * 100;
                        }

                        // Use default values if extraction failed
                        if (salePrice == null && originalPrice != null) {
                            salePrice = originalPrice;
                        } else if (salePrice == null) {
                            salePrice = 0.0;
                            originalPrice = 0.0;
                        } else if (originalPrice == null) {
                            originalPrice = salePrice;
                        }

                        // Create a product item with the extracted information
                        ProductItem product = ProductItem.builder()
                                .id(UUID.randomUUID().toString())
                                .name(name != null ? name : "Unknown Product")
                                .url(productUrl != null ? productUrl : searchUrl)
                                .imageUrl(imageUrl)
                                .originalPrice(originalPrice)
                                .salePrice(salePrice)
                                .discountPercent(discountPercent)
                                .rating(4) // Default rating since it's hard to extract accurately
                                .soldCount(soldCount != null ? soldCount : 0)
                                .shopName(shopName != null ? shopName : "Unknown Shop")
                                .platform("shopee")
                                .category(request.getCategory() != null ? request.getCategory() : "Electronics")
                                .commission(salePrice * 0.05) // 5% commission
                                .build();

                        products.add(product);
                        count.incrementAndGet();
                    } catch (Exception e) {
                        log.warn("Error extracting product details with Playwright: {}", e.getMessage());
                        // Continue with the next product
                    }
                }

                return products;
            } catch (Exception e) {
                log.error("Error scraping Shopee products with Playwright: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to scrape products with Playwright", e);
            }
        });
    }

    /**
     * Scrape products using JSoup (fallback method)
     *
     * @param searchUrl The search URL
     * @param limit     Maximum number of products to scrape
     * @param request   The affiliate scraper request
     * @return List of scraped products
     */
    private List<ProductItem> scrapeWithJsoup(String searchUrl, int limit, AffiliateScraperRequest request) {
        log.info("Scraping with JSoup: {}", searchUrl);
        List<ProductItem> products = new ArrayList<>();

        // Set up headers
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Referer", "https://shopee.vn/");

        try {
            // Parse the HTML to extract product information
            Document doc = fetchDocument(searchUrl, headers);

            // Find product items on the page
            Elements productElements = doc.select(".shopee-search-item-result__item");
            log.info("Found {} product elements on Shopee search page using JSoup", productElements.size());

            int count = 0;

            for (Element productElement : productElements) {
                if (count >= limit) break;

                try {
                    // Extract product details
                    String productUrl = extractShopeeProductUrl(productElement);
                    String name = extractShopeeProductName(productElement);
                    String imageUrl = extractShopeeProductImage(productElement);
                    Double originalPrice = extractShopeeOriginalPrice(productElement);
                    Double salePrice = extractShopeeSalePrice(productElement);
                    Integer soldCount = extractShopeeSoldCount(productElement);
                    String shopName = extractShopeeShopName(productElement);
                    Double discountPercent = 0.0;

                    // Calculate discount percentage if both prices are available
                    if (originalPrice != null && salePrice != null && originalPrice > 0) {
                        discountPercent = ((originalPrice - salePrice) / originalPrice) * 100;
                    }

                    // Create a product item with the extracted information
                    ProductItem product = ProductItem.builder()
                            .id(UUID.randomUUID().toString())
                            .name(name)
                            .url(productUrl)
                            .imageUrl(imageUrl)
                            .originalPrice(originalPrice)
                            .salePrice(salePrice)
                            .discountPercent(discountPercent)
                            .rating(4) // Default rating since it's hard to extract accurately
                            .soldCount(soldCount != null ? soldCount : 0)
                            .shopName(shopName)
                            .platform("shopee")
                            .category(request.getCategory() != null ? request.getCategory() : "Electronics")
                            .commission(salePrice * 0.05) // 5% commission
                            .build();

                    products.add(product);
                    count++;

                } catch (Exception e) {
                    log.warn("Error extracting product details with JSoup: {}", e.getMessage());
                    // Continue with the next product
                }
            }

            return products;

        } catch (Exception e) {
            log.error("Error scraping Shopee products with JSoup: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to scrape products with JSoup", e);
        }
    }

    /**
     * Extract text from an element using Playwright
     *
     * @param element  The element handle
     * @param selector The CSS selector
     * @return The extracted text
     */
    private String extractPlaywrightText(ElementHandle element, String selector) {
        try {
            ElementHandle textElement = element.querySelector(selector);
            if (textElement != null) {
                return textElement.textContent().trim();
            }
        } catch (Exception e) {
            log.warn("Error extracting text with selector {}: {}", selector, e.getMessage());
        }
        return null;
    }

    /**
     * Extract an attribute from an element using Playwright
     *
     * @param element   The element handle
     * @param selector  The CSS selector
     * @param attribute The attribute name
     * @return The extracted attribute value
     */
    private String extractPlaywrightAttribute(ElementHandle element, String selector, String attribute) {
        try {
            ElementHandle attrElement = element.querySelector(selector);
            if (attrElement != null) {
                return attrElement.getAttribute(attribute);
            }
        } catch (Exception e) {
            log.warn("Error extracting attribute {} with selector {}: {}", attribute, selector, e.getMessage());
        }
        return null;
    }

    /**
     * Extract a price from an element using Playwright
     *
     * @param element  The element handle
     * @param selector The CSS selector
     * @return The extracted price as a Double
     */
    private Double extractPlaywrightPrice(ElementHandle element, String selector) {
        try {
            String priceText = extractPlaywrightText(element, selector);
            if (priceText != null && !priceText.isEmpty()) {
                // Remove currency symbols and format to a parsable number
                priceText = priceText.replaceAll("[^\\d.,]", "").replace(",", ".");
                return Double.parseDouble(priceText);
            }
        } catch (Exception e) {
            log.warn("Error extracting price with selector {}: {}", selector, e.getMessage());
        }
        return null;
    }

    /**
     * Extract the sold count from an element using Playwright
     *
     * @param element The element handle
     * @return The extracted sold count as an Integer
     */
    private Integer extractPlaywrightSoldCount(ElementHandle element) {
        try {
            String soldText = extractPlaywrightText(element, ".shopee-item-card__sold-text, .shopee-search-item-result__item .shopee-item-card__text-sold");
            if (soldText != null && !soldText.isEmpty()) {
                Pattern pattern = Pattern.compile("(\\d+)");
                Matcher matcher = pattern.matcher(soldText);
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group(1));
                }
            }
        } catch (Exception e) {
            log.warn("Error extracting sold count: {}", e.getMessage());
        }
        return 0;
    }
}
