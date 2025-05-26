package com.authenhub.service.crawler.provider;

import com.authenhub.bean.crawl.DataCrawlRequest;
import com.authenhub.bean.crawl.NewsBean;
import com.authenhub.config.wp.WpSite;
import com.authenhub.config.application.JsonMapper;
import com.authenhub.exception.ErrorApiException;
import com.authenhub.service.crawler.AbstractCrawler;
import com.authenhub.service.helper.GoogleApiHelper;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static com.authenhub.constant.Constant.ENTER;

@Slf4j
@Component("vnexpress")
public class VnExpressCrawler extends AbstractCrawler {

    private static final String VNEXPRESS_URL = "https://vnexpress.net/cong-nghe";
    private static final Random random = new Random();
    private final LoadingCache<DataCrawlRequest, List<NewsBean>> CACHE_DATA_CRAWL = Caffeine.newBuilder()
            .build(this::getData);

    public VnExpressCrawler(RestTemplate restTemplate, WpSite wpSite, JsonMapper jsonMapper, GoogleApiHelper googleApiHelper) {
        super(restTemplate, wpSite, jsonMapper, googleApiHelper);
    }

    @Override
    public List<NewsBean> getData(DataCrawlRequest wpPublishPostBean) throws IOException {
        log.info("Begin crawling VnExpress data with request {}", wpPublishPostBean);
        String siteUrl = VNEXPRESS_URL;
        if (StringUtils.isNotEmpty(wpPublishPostBean.getSiteUrl())) {
            siteUrl = wpPublishPostBean.getSiteUrl();
        }
        Document homepage = fetchDocument(siteUrl);
        Elements newsElements = extractElements(homepage, "article.item-news.item-news-common");

        log.info("Total news found: {}", newsElements.size());
        int limitElement = Objects.nonNull(wpPublishPostBean.getLimit()) ? wpPublishPostBean.getLimit() : Integer.MAX_VALUE;
        List<CompletableFuture<NewsBean>> futures = newsElements.stream()
                .limit(limitElement)
                .map(this::parseDocumentElementAsync)
                .toList();

        var result = futures.stream()
                .map(future -> future.exceptionally(e -> {
                    log.error("Error processing future: {}", e.getMessage());
                    return null;
                }))
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();

        log.info("Final get total news {}", result.size());
        return result;
    }

    @Override
    public CompletableFuture<Object> exportExcel(DataCrawlRequest wpPublishPostBean) {
        log.info("Begin export to excel data crawl with request {}", jsonMapper.toJson(wpPublishPostBean));
        long startTime = System.currentTimeMillis();
        List<NewsBean> newsBeanList = CACHE_DATA_CRAWL.get(wpPublishPostBean);

        if (newsBeanList.isEmpty()) {
            log.info("Data got is empty");
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String filePath = getFileName();
                createDirectoryIfNotExists(filePath);

                try (OutputStream os = new FileOutputStream(filePath)) {
                    writeExcelFile(os, newsBeanList);
                }

                log.info("Write output stream to file excel success. Time handler: {}ms", (System.currentTimeMillis() - startTime));

                return processFileResponse(filePath, wpPublishPostBean);
            } catch (IOException e) {
                log.error("Write data to excel file have exception", e);
                throw new ErrorApiException("111", e.getMessage());
            }
        });
    }

    @Override
    public void importToWp(DataCrawlRequest wpPublishPostBean) {
        log.info("Begin import to wp");
        List<NewsBean> newsBeanList = CACHE_DATA_CRAWL.get(wpPublishPostBean);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(wpSite.getUsername(), wpSite.getPassword());

        Map<String, Long> uploadedImagesCache = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> futures = newsBeanList.parallelStream()
                .map(newsBean -> {
                    MDC.put("token", NanoIdUtils.randomNanoId());
                    return CompletableFuture.runAsync(() -> processPost(newsBean, headers, uploadedImagesCache));
                })
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void processPost(NewsBean newsBean, HttpHeaders headers, Map<String, Long> uploadedImagesCache) {
        try {
            String thumbnailUrl = newsBean.getThumbnail();
            Long thumbnailId = uploadedImagesCache.computeIfAbsent(thumbnailUrl,
                    k -> uploadImage(thumbnailUrl).getId()); // Cache ảnh đã upload

            // Chuẩn bị dữ liệu bài viết
            Map<String, Object> postData = new HashMap<>();
            postData.put("title", newsBean.getTitle());
            postData.put("content", buildContentWithImages(newsBean.getContent()));
            postData.put("excerpt", newsBean.getDescription());
            postData.put("status", "publish");
            postData.put("featured_media", thumbnailId);
            postData.put("custom_fields", Map.of("original_link", newsBean.getLink()));

            // Gửi request đăng bài
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(postData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    wpSite.getWordpressUrl() + "/wp-json/wp/v2/posts", request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Đăng bài thành công: {}", newsBean.getTitle());
            } else {
                log.error("Lỗi khi đăng bài: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý bài viết {}: {}", newsBean.getTitle(), e.getMessage());
        }
    }


    public CompletableFuture<NewsBean> parseDocumentElementAsync(Element element) {
        return CompletableFuture.supplyAsync(() -> {
            MDC.put("token", NanoIdUtils.randomNanoId());
            return parseDocumentElement(element);
        });
    }

    private NewsBean parseDocumentElement(Element element) {
        try {
            Element articleLink = extractElement(element, ".title-news a[href]");
            if (articleLink == null) return null;

            String link = extractAttribute(articleLink, "href");
            String title = articleLink.text();
            String description = extractText(element, "p.description");

            NewsBean newsBean = NewsBean.builder()
                    .link(link)
                    .title(title)
                    .description(description)
                    .build();

            Document articlePage = fetchDocument(link);
            String content = extractContent(articlePage);
            List<String> imageUrls = extractImages(articlePage);
            String thumbnail = extractThumbnail(element);
            String featureImg = imageUrls.isEmpty() ? thumbnail : imageUrls.get(random.nextInt(imageUrls.size()));
            newsBean.setThumbnail(featureImg);
            newsBean.setContent(content);
            newsBean.setImages(imageUrls);

            log.info("Crawled post: [{}] success ", title);
            return newsBean;
        } catch (IOException e) {
            log.error("Error crawling article: {}", e.getMessage());
            return null;
        }
    }

    private String extractThumbnail(Element element) {
        Element img = extractElement(element, "div.thumb-art img[itemprop=contentUrl]");
        if (img != null) {
            String thumbnail = extractAttribute(img, "data-src");
            return StringUtils.isEmpty(thumbnail) ? extractAttribute(img, "src") : thumbnail;
        }
        Element video = extractElement(element, "div.thumb-art video[itemprop=contentUrl]");
        if (video != null) {
            String poster = video.attr("poster");
            return StringUtils.isEmpty(poster) ? extractAttribute(video, "src") : poster;
        }
        return null;
    }

    private String extractContent(Document doc) {
        Elements contentElements = doc.select("article.fck_detail p");
        StringBuilder contentBuilder = new StringBuilder();
        for (Element p : contentElements) {
            Element figure = p.nextElementSibling();
            if (figure != null && figure.tagName().equals("figure")) {
                Element img = figure.selectFirst("img[itemprop=contentUrl]");
                if (img != null) {
                    contentBuilder
                            .append(p.text())
                            .append(ENTER)
                            .append(img.attr("data-src"))
                            .append(ENTER)
                            .append("alt:")
                            .append(img.attr("alt"))
                            .append(ENTER);
                }
            } else {
                String text = p.select(".Normal").text();
                contentBuilder.append(text).append(ENTER);
            }
        }
        return contentBuilder.toString();
    }

    private List<String> extractImages(Document doc) {
        List<String> imageUrls = new ArrayList<>();
        Elements imageMetaTags = doc.select("figure meta[itemprop=url]");
        imageMetaTags.forEach(img -> {
            String url = img.attr("content");
            if (!url.isEmpty()) imageUrls.add(url);
        });
        return imageUrls;
    }
}
