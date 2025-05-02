package com.authenhub.service;

import com.authenhub.bean.proxy.CheckResult;
import com.authenhub.bean.proxy.ImportResult;
import com.authenhub.bean.proxy.ProxyRequest;
import com.authenhub.bean.proxy.ProxyResponse;
import com.authenhub.entity.User;
import com.authenhub.entity.mongo.FreeProxy;
import com.authenhub.repository.FreeProxyRepository;
import com.authenhub.service.interfaces.IFreeProxyService;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeProxyService implements IFreeProxyService {

    private final UserContext userContext;
    private final FreeProxyRepository proxyRepository;

    @Override
    public List<ProxyResponse> getAllProxies() {
        return proxyRepository.findAll().stream()
                .map(ProxyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProxyResponse> getActiveProxies() {
        return proxyRepository.findByIsActiveTrue().stream()
                .map(ProxyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ProxyResponse getProxyById(String id) {
        FreeProxy proxy = proxyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proxy not found"));
        return ProxyResponse.fromEntity(proxy);
    }

    @Override
    public ProxyResponse createProxy(ProxyRequest request, String token) {
        log.info("Token get form header {}", token);
//        String usernameFromToken = userContext.getCurrentUsername();
//        log.info("Username from token: {}", usernameFromToken);
        // Extract username from token
        User user = userContext.getCurrentUser();
        log.info("Current user get by context {}", user.getFullName());
        FreeProxy proxy = new FreeProxy();
        proxy.setIpAddress(request.getIpAddress());
        proxy.setPort(request.getPort());
        proxy.setProtocol(request.getProtocol());
        proxy.setCountry(request.getCountry());
        proxy.setCity(request.getCity());
        proxy.setNotes(request.getNotes());
        proxy.setActive(true);
        proxy.setCreatedAt(TimestampUtils.now());
        proxy.setUpdatedAt(TimestampUtils.now());
        proxy.setCreatedBy(user.getFullName());
        proxy.setSuccessCount(0);
        proxy.setFailCount(0);
        proxy.setUptime(0.0);

        // Check the proxy before saving
        CheckResult checkResult = checkProxy(proxy);
        proxy.setResponseTimeMs(checkResult.getResponseTimeMs());
        proxy.setLastChecked(checkResult.getCheckedAt());
        proxy.setActive(checkResult.isWorking());

        if (checkResult.isWorking()) {
            proxy.setSuccessCount(1);
            proxy.setUptime(100.0);
        } else {
            proxy.setFailCount(1);
            proxy.setUptime(0.0);
        }

        FreeProxy savedProxy = proxyRepository.save(proxy);
        return ProxyResponse.fromEntity(savedProxy);
    }

    @Override
    public ProxyResponse updateProxy(String id, ProxyRequest request) {
        FreeProxy proxy = proxyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proxy not found"));

        proxy.setIpAddress(request.getIpAddress());
        proxy.setPort(request.getPort());
        proxy.setProtocol(request.getProtocol());
        proxy.setCountry(request.getCountry());
        proxy.setCity(request.getCity());
        proxy.setNotes(request.getNotes());
        proxy.setUpdatedAt(TimestampUtils.now());

        FreeProxy savedProxy = proxyRepository.save(proxy);
        return ProxyResponse.fromEntity(savedProxy);
    }

    @Override
    public void deleteProxy(String id) {
        if (!proxyRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proxy not found");
        }
        proxyRepository.deleteById(id);
    }

    @Override
    public CheckResult checkProxyById(String id) {
        FreeProxy proxy = proxyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proxy not found"));

        CheckResult result = checkProxy(proxy);

        // Update proxy stats
        if (result.isWorking()) {
            proxy.setSuccessCount(proxy.getSuccessCount() + 1);
        } else {
            proxy.setFailCount(proxy.getFailCount() + 1);
        }

        int totalChecks = proxy.getSuccessCount() + proxy.getFailCount();
        proxy.setUptime((double) proxy.getSuccessCount() / totalChecks * 100);
        proxy.setResponseTimeMs(result.getResponseTimeMs());
        proxy.setLastChecked(result.getCheckedAt());
        proxy.setActive(result.isWorking());
        proxy.setUpdatedAt(TimestampUtils.now());

        proxyRepository.save(proxy);
        return result;
    }

    @Override
    public ImportResult importProxies(MultipartFile file, String fileType) {
        return null;
    }

    @Override
    public List<ProxyResponse> getProxiesByProtocol(String protocol) {
        return proxyRepository.findByProtocol(protocol).stream()
                .map(ProxyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProxyResponse> getProxiesByCountry(String country) {
        return proxyRepository.findByCountry(country).stream()
                .map(ProxyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProxyResponse> getFastProxies(int maxResponseTime) {
        return proxyRepository.findByResponseTimeLessThan(maxResponseTime).stream()
                .map(ProxyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProxyResponse> getReliableProxies(double minUptime) {
        return proxyRepository.findByUptimeGreaterThan(minUptime).stream()
                .map(ProxyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private CheckResult checkProxy(FreeProxy proxy) {
        boolean isWorking;
        int responseTime = 0;
        Timestamp checkedAt = TimestampUtils.now();

        try {
            long startTime = System.currentTimeMillis();
            Proxy javaProxy = configureProxy(proxy);
            if (Objects.isNull(javaProxy)) {
                return CheckResult.builder()
                        .id(proxy.getId())
                        .isWorking(false)
                        .responseTimeMs(0)
                        .checkedAt(checkedAt)
                        .build();
            }
            HttpClient client = HttpClient.newBuilder()
                    .proxy(ProxySelector.of((InetSocketAddress) javaProxy.address()))
                    .connectTimeout(java.time.Duration.ofSeconds(10))
                    .build();

            // Create HTTP request to a test URL
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://api.ipify.org"))
                    .timeout(java.time.Duration.ofSeconds(10))
                    .GET()
                    .build();

            // Send request and measure response
            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int responseCode = httpResponse.statusCode();
            long endTime = System.currentTimeMillis();

            responseTime = (int) (endTime - startTime);
            isWorking = (responseCode >= 200 && responseCode < 300);

            return CheckResult.builder()
                    .id(proxy.getId())
                    .isWorking(isWorking)
                    .responseTimeMs(responseTime)
                    .checkedAt(checkedAt)
                    .build();
        } catch (Exception e) {
            log.error("Error checking proxy: {}", e.getMessage());
            isWorking = false;
        }

        return CheckResult.builder()
                .id(proxy.getId())
                .isWorking(isWorking)
                .responseTimeMs(responseTime)
                .checkedAt(checkedAt)
                .build();
    }

    @Scheduled(fixedRate = 3600000) // Check every hour
    public void checkAllProxies() {
        log.info("Starting scheduled check of all proxies");
        List<FreeProxy> proxies = proxyRepository.findAll();

        for (FreeProxy proxy : proxies) {
            try {
                CheckResult result = checkProxy(proxy);

                // Update proxy stats
                if (result.isWorking()) {
                    proxy.setSuccessCount(proxy.getSuccessCount() + 1);
                } else {
                    proxy.setFailCount(proxy.getFailCount() + 1);
                }

                int totalChecks = proxy.getSuccessCount() + proxy.getFailCount();
                proxy.setUptime((double) proxy.getSuccessCount() / totalChecks * 100);
                proxy.setResponseTimeMs(result.getResponseTimeMs());
                proxy.setLastChecked(result.getCheckedAt());
                proxy.setActive(result.isWorking());
                proxy.setUpdatedAt(TimestampUtils.now());

                proxyRepository.save(proxy);
                log.info("Checked proxy {}: {}", proxy.getId(), result.isWorking() ? "working" : "not working");
            } catch (Exception e) {
                log.error("Error during scheduled check of proxy {}: {}", proxy.getId(), e.getMessage());
            }
        }

        log.info("Completed scheduled check of all proxies");
    }

    private Proxy configureProxy(FreeProxy request) throws IllegalArgumentException {
        InetSocketAddress address = new InetSocketAddress(request.getIpAddress(), request.getPort());
        String protocol = request.getProtocol().toUpperCase();

        Proxy proxy;
        switch (protocol) {
            case "HTTP":
            case "HTTPS":
                proxy = new Proxy(Proxy.Type.HTTP, address);
                break;
            case "SOCKS4":
            case "SOCKS5":
                proxy = new Proxy(Proxy.Type.SOCKS, address);
                break;
            default:
                proxy = null;
                break;
        }
        return proxy;
    }

//    public FreeProxyDto.ImportResult importProxiesFromFile(MultipartFile file, String fileType, String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        if (file.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
//        }
//
//        try {
//            if ("CSV".equalsIgnoreCase(fileType)) {
//                return importFromCsv(file, user);
//            } else if ("EXCEL".equalsIgnoreCase(fileType)) {
//                return importFromExcel(file, user);
//            } else {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported file type. Use CSV or EXCEL");
//            }
//        } catch (IOException e) {
//            log.error("Error reading file", e);
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading file: " + e.getMessage());
//        }
//    }

//    private FreeProxyDto.ImportResult importFromCsv(MultipartFile file, User user) throws IOException {
//        List<FreeProxyDto.ImportError> errors = new ArrayList<>();
//        List<FreeProxyDto.Response> importedProxies = new ArrayList<>();
//        int totalProcessed = 0;
//        int successCount = 0;
//        int failCount = 0;
//
//        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
//            String[] header = reader.readNext(); // Skip header row
//            String[] line;
//            int rowNumber = 1; // Start from row 1 (after header)
//
//            while ((line = reader.readNext()) != null) {
//                rowNumber++;
//                totalProcessed++;
//
//                try {
//                    if (line.length < 3) {
//                        throw new IllegalArgumentException("Row has insufficient columns. Expected at least 3 (IP, Port, Protocol)");
//                    }
//
//                    String ipAddress = line[0].trim();
//                    int port;
//                    try {
//                        port = Integer.parseInt(line[1].trim());
//                    } catch (NumberFormatException e) {
//                        throw new IllegalArgumentException("Invalid port number: " + line[1]);
//                    }
//
//                    String protocol = line[2].trim().toUpperCase();
//                    if (!isValidProtocol(protocol)) {
//                        throw new IllegalArgumentException("Invalid protocol: " + protocol + ". Must be one of HTTP, HTTPS, SOCKS4, SOCKS5");
//                    }
//
//                    String country = line.length > 3 ? line[3].trim() : null;
//                    String city = line.length > 4 ? line[4].trim() : null;
//                    String notes = line.length > 5 ? line[5].trim() : null;
//
//                    FreeProxyDto.Request proxyRequest = FreeProxyDto.Request.builder()
//                            .ipAddress(ipAddress)
//                            .port(port)
//                            .protocol(protocol)
//                            .country(country)
//                            .city(city)
//                            .notes(notes)
//                            .build();
//
//                    FreeProxyDto.Response createdProxy = createProxy(proxyRequest, user.getUsername());
//                    importedProxies.add(createdProxy);
//                    successCount++;
//                } catch (Exception e) {
//                    failCount++;
//                    String rawData = String.join(",", line);
//                    errors.add(FreeProxyDto.ImportError.builder()
//                            .rowNumber(rowNumber)
//                            .errorMessage(e.getMessage())
//                            .rawData(rawData)
//                            .build());
//                }
//            }
//        } catch (CsvValidationException e) {
//            throw new IOException("Error parsing CSV file: " + e.getMessage(), e);
//        }
//
//        return FreeProxyDto.ImportResult.builder()
//                .totalProcessed(totalProcessed)
//                .successCount(successCount)
//                .failCount(failCount)
//                .errors(errors)
//                .importedProxies(importedProxies)
//                .build();
//    }
//
//    private FreeProxyDto.ImportResult importFromExcel(MultipartFile file, User user) throws IOException {
//        List<FreeProxyDto.ImportError> errors = new ArrayList<>();
//        List<FreeProxyDto.Response> importedProxies = new ArrayList<>();
//        int totalProcessed = 0;
//        int successCount = 0;
//        int failCount = 0;
//
//        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
//            Sheet sheet = workbook.getSheetAt(0);
//            int rowNumber = 0;
//
//            // Skip header row
//            for (Row row : sheet) {
//                if (rowNumber == 0) {
//                    rowNumber++;
//                    continue;
//                }
//
//                totalProcessed++;
//
//                try {
//                    Cell ipCell = row.getCell(0);
//                    Cell portCell = row.getCell(1);
//                    Cell protocolCell = row.getCell(2);
//
//                    if (ipCell == null || portCell == null || protocolCell == null) {
//                        throw new IllegalArgumentException("Missing required fields (IP, Port, or Protocol)");
//                    }
//
//                    String ipAddress = getCellValueAsString(ipCell);
//
//                    int port;
//                    try {
//                        if (portCell.getCellType() == CellType.NUMERIC) {
//                            port = (int) portCell.getNumericCellValue();
//                        } else {
//                            port = Integer.parseInt(getCellValueAsString(portCell));
//                        }
//                    } catch (NumberFormatException e) {
//                        throw new IllegalArgumentException("Invalid port number: " + getCellValueAsString(portCell));
//                    }
//
//                    String protocol = getCellValueAsString(protocolCell).toUpperCase();
//                    if (!isValidProtocol(protocol)) {
//                        throw new IllegalArgumentException("Invalid protocol: " + protocol + ". Must be one of HTTP, HTTPS, SOCKS4, SOCKS5");
//                    }
//
//                    String country = row.getCell(3) != null ? getCellValueAsString(row.getCell(3)) : null;
//                    String city = row.getCell(4) != null ? getCellValueAsString(row.getCell(4)) : null;
//                    String notes = row.getCell(5) != null ? getCellValueAsString(row.getCell(5)) : null;
//
//                    FreeProxyDto.Request proxyRequest = FreeProxyDto.Request.builder()
//                            .ipAddress(ipAddress)
//                            .port(port)
//                            .protocol(protocol)
//                            .country(country)
//                            .city(city)
//                            .notes(notes)
//                            .build();
//
//                    FreeProxyDto.Response createdProxy = createProxy(proxyRequest, user.getUsername());
//                    importedProxies.add(createdProxy);
//                    successCount++;
//                } catch (Exception e) {
//                    failCount++;
//                    StringBuilder rawData = new StringBuilder();
//                    for (Cell cell : row) {
//                        rawData.append(getCellValueAsString(cell)).append(",");
//                    }
//
//                    errors.add(FreeProxyDto.ImportError.builder()
//                            .rowNumber(rowNumber)
//                            .errorMessage(e.getMessage())
//                            .rawData(rawData.toString())
//                            .build());
//                }
//
//                rowNumber++;
//            }
//        }
//
//        return FreeProxyDto.ImportResult.builder()
//                .totalProcessed(totalProcessed)
//                .successCount(successCount)
//                .failCount(failCount)
//                .errors(errors)
//                .importedProxies(importedProxies)
//                .build();
//    }
//
//    private String getCellValueAsString(Cell cell) {
//        if (cell == null) {
//            return "";
//        }
//
//        switch (cell.getCellType()) {
//            case STRING:
//                return cell.getStringCellValue().trim();
//            case NUMERIC:
//                if (DateUtil.isCellDateFormatted(cell)) {
//                    return cell.getLocalDateTimeCellValue().toString();
//                } else {
//                    return String.valueOf((int) cell.getNumericCellValue());
//                }
//            case BOOLEAN:
//                return String.valueOf(cell.getBooleanCellValue());
//            case FORMULA:
//                return cell.getCellFormula();
//            default:
//                return "";
//        }
//    }
//
//    private boolean isValidProtocol(String protocol) {
//        return "HTTP".equals(protocol) || "HTTPS".equals(protocol) ||
//               "SOCKS4".equals(protocol) || "SOCKS5".equals(protocol);
//    }
}
