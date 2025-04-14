package com.authenhub.service;

import com.authenhub.dto.FreeProxyDto;
import com.authenhub.entity.FreeProxy;
import com.authenhub.entity.User;
import com.authenhub.repository.FreeProxyRepository;
import com.authenhub.repository.UserRepository;
//import com.opencsv.CSVReader;
//import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeProxyService {

    private final FreeProxyRepository proxyRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public List<FreeProxyDto.Response> getAllProxies() {
        return proxyRepository.findAll().stream()
                .map(FreeProxyDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public List<FreeProxyDto.Response> getActiveProxies() {
        return proxyRepository.findByIsActiveTrue().stream()
                .map(FreeProxyDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public FreeProxyDto.Response getProxyById(String id) {
        FreeProxy proxy = proxyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proxy not found"));
        return FreeProxyDto.Response.fromEntity(proxy);
    }

    public FreeProxyDto.Response createProxy(FreeProxyDto.Request request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        FreeProxy proxy = new FreeProxy();
        proxy.setIpAddress(request.getIpAddress());
        proxy.setPort(request.getPort());
        proxy.setProtocol(request.getProtocol());
        proxy.setCountry(request.getCountry());
        proxy.setCity(request.getCity());
        proxy.setNotes(request.getNotes());
        proxy.setActive(true);
        proxy.setCreatedAt(LocalDateTime.now());
        proxy.setUpdatedAt(LocalDateTime.now());
        proxy.setCreatedBy(user.getId());
        proxy.setSuccessCount(0);
        proxy.setFailCount(0);
        proxy.setUptime(0.0);

        // Check the proxy before saving
        FreeProxyDto.CheckResult checkResult = checkProxy(proxy);
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
        return FreeProxyDto.Response.fromEntity(savedProxy);
    }

    public FreeProxyDto.Response updateProxy(String id, FreeProxyDto.Request request) {
        FreeProxy proxy = proxyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proxy not found"));

        proxy.setIpAddress(request.getIpAddress());
        proxy.setPort(request.getPort());
        proxy.setProtocol(request.getProtocol());
        proxy.setCountry(request.getCountry());
        proxy.setCity(request.getCity());
        proxy.setNotes(request.getNotes());
        proxy.setUpdatedAt(LocalDateTime.now());

        FreeProxy savedProxy = proxyRepository.save(proxy);
        return FreeProxyDto.Response.fromEntity(savedProxy);
    }

    public void deleteProxy(String id) {
        if (!proxyRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proxy not found");
        }
        proxyRepository.deleteById(id);
    }

    public FreeProxyDto.CheckResult checkProxyById(String id) {
        FreeProxy proxy = proxyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proxy not found"));

        FreeProxyDto.CheckResult result = checkProxy(proxy);

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
        proxy.setUpdatedAt(LocalDateTime.now());

        proxyRepository.save(proxy);
        return result;
    }

    private FreeProxyDto.CheckResult checkProxy(FreeProxy proxy) {
        boolean isWorking = false;
        int responseTime = 0;
        LocalDateTime checkedAt = LocalDateTime.now();

        try {
            long startTime = System.currentTimeMillis();

            // Create proxy
            Proxy javaProxy = createJavaProxy(proxy);

            // Test connection with timeout
            URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(javaProxy);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            long endTime = System.currentTimeMillis();

            responseTime = (int) (endTime - startTime);
            isWorking = (responseCode >= 200 && responseCode < 300);

            connection.disconnect();
        } catch (Exception e) {
            log.error("Error checking proxy: {}", e.getMessage());
            isWorking = false;
        }

        return FreeProxyDto.CheckResult.builder()
                .id(proxy.getId())
                .isWorking(isWorking)
                .responseTimeMs(responseTime)
                .checkedAt(checkedAt)
                .build();
    }

    private Proxy createJavaProxy(FreeProxy proxy) {
        Proxy.Type proxyType;

        switch (proxy.getProtocol().toUpperCase()) {
            case "SOCKS4":
            case "SOCKS5":
                proxyType = Proxy.Type.SOCKS;
                break;
            case "HTTP":
            case "HTTPS":
            default:
                proxyType = Proxy.Type.HTTP;
                break;
        }

        return new Proxy(proxyType, new InetSocketAddress(proxy.getIpAddress(), proxy.getPort()));
    }

    @Scheduled(fixedRate = 3600000) // Check every hour
    public void checkAllProxies() {
        log.info("Starting scheduled check of all proxies");
        List<FreeProxy> proxies = proxyRepository.findAll();

        for (FreeProxy proxy : proxies) {
            try {
                FreeProxyDto.CheckResult result = checkProxy(proxy);

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
                proxy.setUpdatedAt(LocalDateTime.now());

                proxyRepository.save(proxy);
                log.info("Checked proxy {}: {}", proxy.getId(), result.isWorking() ? "working" : "not working");
            } catch (Exception e) {
                log.error("Error during scheduled check of proxy {}: {}", proxy.getId(), e.getMessage());
            }
        }

        log.info("Completed scheduled check of all proxies");
    }

    public List<FreeProxyDto.Response> getProxiesByProtocol(String protocol) {
        return proxyRepository.findByProtocol(protocol).stream()
                .map(FreeProxyDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public List<FreeProxyDto.Response> getProxiesByCountry(String country) {
        return proxyRepository.findByCountry(country).stream()
                .map(FreeProxyDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public List<FreeProxyDto.Response> getFastProxies(int maxResponseTime) {
        return proxyRepository.findByResponseTimeLessThan(maxResponseTime).stream()
                .map(FreeProxyDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public List<FreeProxyDto.Response> getReliableProxies(double minUptime) {
        return proxyRepository.findByUptimeGreaterThan(minUptime).stream()
                .map(FreeProxyDto.Response::fromEntity)
                .collect(Collectors.toList());
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
