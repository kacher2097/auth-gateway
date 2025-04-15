package com.authenhub.service;

import com.authenhub.bean.WpMediaResponse;
import com.authenhub.bean.crawl.DataCrawlRequest;
import com.authenhub.bean.crawl.NewsBean;
import com.authenhub.config.WpSite;
import com.authenhub.config.application.JsonMapper;
import com.authenhub.constant.Constant;
import com.authenhub.service.helper.GoogleApiHelper;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dhatim.fastexcel.BorderStyle;
import org.dhatim.fastexcel.Color;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.authenhub.constant.Constant.ENTER;
import static com.authenhub.constant.Constant.TIMES_NEW_ROMAN;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCrawler implements HandleCrawlService {
    protected static final String USER_AGENT = "Chrome/133.0.0.0 Safari/537.36";
    protected static final int TIMEOUT = 10000;

    protected final RestTemplate restTemplate;
    protected final WpSite wpSite;
    protected final JsonMapper jsonMapper;
    protected final GoogleApiHelper googleApiHelper;

    protected Document fetchDocument(String url) throws IOException {
        log.info("Fetching document from {}", url);
        return Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT)
                .get();
    }

    protected Elements extractElements(Document doc, String cssSelector) {
        return Objects.nonNull(doc) ? doc.select(cssSelector) : null;
    }

    protected Element extractElement(Document doc, String cssSelector) {
        return Objects.nonNull(doc) ? doc.selectFirst(cssSelector) : null;
    }

    protected Element extractElement(Element element, String cssSelector) {
        return Objects.nonNull(element) ? element.selectFirst(cssSelector) : null;
    }

    protected String extractText(Element element, String cssSelector) {
        if (StringUtils.isEmpty(cssSelector)) return StringUtils.EMPTY;
        return Objects.nonNull(element) && element.selectFirst(cssSelector) != null ? element.selectFirst(cssSelector).text() : StringUtils.EMPTY;
    }

    protected String extractAttribute(Element element, String cssSelector, String attribute) {
        if (StringUtils.isEmpty(cssSelector)) return StringUtils.EMPTY;
        return Objects.nonNull(element) && element.selectFirst(cssSelector) != null ? element.selectFirst(cssSelector).attr(attribute) : StringUtils.EMPTY;
    }

    protected String extractAttribute(Element element, String attribute) {
        if (StringUtils.isEmpty(attribute)) return StringUtils.EMPTY;
        return Objects.nonNull(element) ? element.attr(attribute) : StringUtils.EMPTY;
    }

    protected void writeGGSheets(String filePath) {
        googleApiHelper.exportExcelToGoogle(filePath);
    }

    protected WpMediaResponse uploadImage(String imageUrl) {
        log.info("Begin upload image for url {}", imageUrl);
        try {
            InputStream imageStream = downloadImage(imageUrl);

            String pathPart = imageUrl.substring(0, imageUrl.lastIndexOf("?"));
            String fileName = pathPart.substring(pathPart.lastIndexOf("/") + 1);
            log.info("File name with ext {}", fileName);
            String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1);
            MediaType mediaType = fromFileTypeToMimeType(fileExt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.set("Content-Disposition", "attachment; filename=" + fileName);
            headers.setBasicAuth(wpSite.getUsername(), wpSite.getPassword());

            HttpEntity<InputStreamResource> request = new HttpEntity<>(
                    new InputStreamResource(imageStream), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    wpSite.getWordpressUrl() + "/wp-json/wp/v2/media",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && StringUtils.isNotEmpty(response.getBody())) {
                log.info("Response upload image {}", response.getBody());
                WpMediaResponse wpMediaResponse = jsonMapper.fromJson(response.getBody(), WpMediaResponse.class);
                log.info("SRC after upload {} and id {} ", wpMediaResponse.getSourceUrl(), wpMediaResponse.getId());
                return wpMediaResponse;
            }
        } catch (Exception e) {
            log.error("Error when upload image: {}", e.getMessage());
        }
        return null;
    }

    protected InputStream downloadImage(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);
        return connection.getInputStream();
    }

    protected String buildContentWithImages(String contentOriginal) {
        StringBuilder newContent = new StringBuilder();
        String[] contentRaw = contentOriginal.split(Constant.ENTER);

        for (int i = 0; i < contentRaw.length; i++) {
            String content = contentRaw[i].trim();
            if (StringUtils.isEmpty(content)) continue;

            if (content.startsWith("https://") || content.startsWith("http://")) {
                WpMediaResponse wpMediaResponse = uploadImage(content);
                String newImageUrl = wpMediaResponse.getSourceUrl();
                String altText = "Mô tả ảnh minh họa";
                if (i + 1 < contentRaw.length && contentRaw[i + 1].trim().startsWith("alt:")) {
                    altText = contentRaw[i + 1].trim().substring(4).trim();
                    i++;
                }

                if (newImageUrl != null) {
                    String imgTag = "<figure class=\"custom-image\">" +
                            "<img src=\"" + newImageUrl + "\" alt=\"" + altText + "\" />" +
                            "<figcaption class=\"custom-caption\">" + altText + "</figcaption>" +
                            "</figure>";
                    newContent.append(imgTag).append(Constant.ENTER);
                }

                continue;
            }
            newContent.append("<p>").append(content).append("</p>").append(Constant.ENTER);
        }
        return newContent.toString();
    }

    protected void writeExcelFile(OutputStream os, List<NewsBean> newsBeanList) throws IOException {
        var wb = new Workbook(os, "Author: Tuan Le", "1.0");
        Worksheet ws = wb.newWorksheet("Sheet 1");

        configWidth(ws);
        buildTitleSheet(ws);
        buildHeaderTable(ws);

        int row = 3;
        int index = 1;
        for (NewsBean item : newsBeanList) {
            writeNewsBeanToRow(ws, row++, index++, item);
        }

        wb.finish();
        ws.finish();
    }

    protected void writeNewsBeanToRow(Worksheet ws, int row, int index, NewsBean item) {
        int col = 0;
        ws.value(row, col++, index);
        ws.value(row, col++, item.getTitle());
        ws.value(row, col++, item.getDescription());
        ws.value(row, col++, item.getThumbnail());
        ws.value(row, col++, item.getLink());
        ws.value(row, col++, item.getContent());
        ws.value(row, col++, String.join("\n ", item.getImages()));

        ws.range(row, 0, row, col - 1).style()
                .horizontalAlignment(Constant.ALIGN_LEFT)
                .verticalAlignment(Constant.ALIGN_CENTER)
                .fontName(Constant.SEGOE_UI)
                .fontSize(10)
                .wrapText(true)
                .borderStyle(BorderStyle.THIN)
                .set();
    }

    protected String getFileName() {
        StringBuilder fileName = new StringBuilder();
        fileName.append(Constant.SLASH)
                .append("upload")
                .append(Constant.SLASH)
                .append(LocalDate.now().format(Constant.DATE_PATH))
                .append(Constant.SLASH)
                .append("Result crawl VNExpress")
                .append(NanoIdUtils.randomNanoId())
                .append(Constant.FILE_EXPORT_EXTENSION);
        log.info("File name export: {}", fileName);
        return String.valueOf(fileName);
    }

    protected void createDirectoryIfNotExists(String filePath) throws IOException {
        Path dirPath = Paths.get(filePath).getParent();
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }


    protected ResponseEntity<FileSystemResource> processFileResponse(String filePath, DataCrawlRequest wpPublishPostBean) throws IOException {
        FileSystemResource file = new FileSystemResource(filePath);
        if (Boolean.TRUE.equals(wpPublishPostBean.getIsWriteSheets())) {
            writeGGSheets(filePath);
            return null;
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=crawl-result-" + wpPublishPostBean.getSource() + ".xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(file);
        }
    }

    protected void configWidth(Worksheet ws) {
        ws.width(0, 10);
        ws.width(1, 20);
        ws.width(2, 25);
        ws.width(3, 20);
        ws.width(4, 20);
        ws.width(5, 100);
        ws.width(6, 20);
    }

    protected void buildTitleSheet(Worksheet ws) {
        log.info("Begin build title of sheet");
        ws.value(0, 0, "DANH SÁCH CÁC BÀI VIẾT CRAWL VNEXPRESS");
        ws.range(0, 0, 0, 4).style()
                .bold()
                .horizontalAlignment(Constant.ALIGN_CENTER)
                .fontSize(14)
                .fontColor(Color.WHITE)
                .fontName(TIMES_NEW_ROMAN)
                .fillColor(Color.PEWTER_BLUE)
                .merge()
                .set();
        log.info("End build title");
    }

    protected void buildHeaderTable(Worksheet ws) {
        log.info("Begin build header of table");
        ws.value(2, 0, "STT");
        ws.value(2, 1, "Tiêu đề");
        ws.value(2, 2, "Mô tả ngắn");
        ws.value(2, 3, "Thumbnail");
        ws.value(2, 4, "Link bài viết");
        ws.value(2, 5, "Nội dung bài viết");
        ws.value(2, 6, "Danh sách hình ảnh");

        ws.range(2, 0, 2, 6).style()
                .bold()
                .horizontalAlignment(Constant.ALIGN_CENTER)
                .verticalAlignment(Constant.ALIGN_CENTER)
                .fontSize(12)
                .fontName(TIMES_NEW_ROMAN)
                .wrapText(true)
                .borderStyle(BorderStyle.THIN)
                .set();

        log.info("End build header of table");
    }

    protected MediaType fromFileTypeToMimeType(String fileType) {
        Map<String, String> fileTypeToMime = new HashMap<>();
        fileTypeToMime.put("rar", "application/vnd.rar");
        fileTypeToMime.put("zip", "application/zip");
        fileTypeToMime.put("png", "image/png");
        fileTypeToMime.put("pdf", "application/pdf");
        fileTypeToMime.put("jpg", "image/jpeg");
        fileTypeToMime.put("jpeg", "image/jpeg");
        fileTypeToMime.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        String mimeType = fileTypeToMime.getOrDefault(fileType.toLowerCase(), "application/octet-stream");
        return MediaType.parseMediaType(mimeType);
    }
}
