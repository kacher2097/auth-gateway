package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.dto.inventory.CreateInventoryRequest;
import com.authenhub.dto.inventory.InventoryDTO;
import com.authenhub.dto.inventory.UpdateInventoryRequest;
import com.authenhub.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "API để quản lý hàng tồn kho")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả hàng tồn kho",
            description = "Trả về danh sách tất cả các mặt hàng trong kho.")
    public ApiResponse<List<InventoryDTO>> getAllInventory() {
        log.info("REST request to get all inventory items");
        List<InventoryDTO> inventory = inventoryService.getAllInventory();
        return ApiResponse.success(inventory);
    }

    @PostMapping("/list")
    @Operation(summary = "Lấy danh sách tất cả hàng tồn kho (POST)",
            description = "Trả về danh sách tất cả các mặt hàng trong kho sử dụng phương thức POST.")
    public ApiResponse<List<InventoryDTO>> getAllInventoryPost() {
        log.info("REST request to get all inventory items (POST)");
        List<InventoryDTO> inventory = inventoryService.getAllInventory();
        return ApiResponse.success(inventory);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy hàng tồn kho theo ID",
            description = "Trả về thông tin chi tiết của một mặt hàng dựa trên ID.")
    public ApiResponse<InventoryDTO> getInventoryById(@PathVariable Long id) {
        log.info("REST request to get inventory item with id: {}", id);
        InventoryDTO inventory = inventoryService.getInventoryById(id);
        return ApiResponse.success(inventory);
    }

    @PostMapping("/{id}")
    @Operation(summary = "Lấy hàng tồn kho theo ID (POST)",
            description = "Trả về thông tin chi tiết của một mặt hàng dựa trên ID sử dụng phương thức POST.")
    public ApiResponse<InventoryDTO> getInventoryByIdPost(@PathVariable Long id) {
        log.info("REST request to get inventory item with id: {} (POST)", id);
        InventoryDTO inventory = inventoryService.getInventoryById(id);
        return ApiResponse.success(inventory);
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Lấy hàng tồn kho theo SKU",
            description = "Trả về thông tin chi tiết của một mặt hàng dựa trên mã SKU.")
    public ApiResponse<InventoryDTO> getInventoryBySku(@PathVariable String sku) {
        log.info("REST request to get inventory item with SKU: {}", sku);
        InventoryDTO inventory = inventoryService.getInventoryBySku(sku);
        return ApiResponse.success(inventory);
    }

    @PostMapping("/sku/{sku}")
    @Operation(summary = "Lấy hàng tồn kho theo SKU (POST)",
            description = "Trả về thông tin chi tiết của một mặt hàng dựa trên mã SKU sử dụng phương thức POST.")
    public ApiResponse<InventoryDTO> getInventoryBySkuPost(@PathVariable String sku) {
        log.info("REST request to get inventory item with SKU: {} (POST)", sku);
        InventoryDTO inventory = inventoryService.getInventoryBySku(sku);
        return ApiResponse.success(inventory);
    }

    @PostMapping
    @Operation(summary = "Tạo mặt hàng mới",
            description = "Tạo một mặt hàng mới trong kho.")
    public ApiResponse<InventoryDTO> createInventory(
            @Valid @RequestBody CreateInventoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to create inventory item with SKU: {}", request.getSku());
        InventoryDTO inventory = inventoryService.createInventory(request, userDetails.getUsername());
        return ApiResponse.success(inventory);
    }

    @PutMapping("/{sku}")
    @Operation(summary = "Cập nhật mặt hàng",
            description = "Cập nhật thông tin của một mặt hàng dựa trên mã SKU.")
    public ApiResponse<InventoryDTO> updateInventory(
            @PathVariable String sku,
            @Valid @RequestBody UpdateInventoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to update inventory item with SKU: {}", sku);
        InventoryDTO inventory = inventoryService.updateInventory(sku, request, userDetails.getUsername());
        return ApiResponse.success(inventory);
    }

    @PostMapping("/update/{sku}")
    @Operation(summary = "Cập nhật mặt hàng (POST)",
            description = "Cập nhật thông tin của một mặt hàng dựa trên mã SKU sử dụng phương thức POST.")
    public ApiResponse<InventoryDTO> updateInventoryPost(
            @PathVariable String sku,
            @Valid @RequestBody UpdateInventoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to update inventory item with SKU: {} (POST)", sku);
        InventoryDTO inventory = inventoryService.updateInventory(sku, request, userDetails.getUsername());
        return ApiResponse.success(inventory);
    }

    @DeleteMapping("/{sku}")
    @Operation(summary = "Xóa mặt hàng",
            description = "Xóa một mặt hàng dựa trên mã SKU.")
    public ApiResponse<Void> deleteInventory(@PathVariable String sku) {
        log.info("REST request to delete inventory item with SKU: {}", sku);
        inventoryService.deleteInventory(sku);
        return ApiResponse.success(null);
    }

    @PostMapping("/delete/{sku}")
    @Operation(summary = "Xóa mặt hàng (POST)",
            description = "Xóa một mặt hàng dựa trên mã SKU sử dụng phương thức POST.")
    public ApiResponse<Void> deleteInventoryPost(@PathVariable String sku) {
        log.info("REST request to delete inventory item with SKU: {} (POST)", sku);
        inventoryService.deleteInventory(sku);
        return ApiResponse.success(null);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Lấy danh sách hàng sắp hết",
            description = "Trả về danh sách các mặt hàng có số lượng thấp hơn ngưỡng cảnh báo.")
    public ApiResponse<List<InventoryDTO>> getLowStockItems() {
        log.info("REST request to get low stock inventory items");
        List<InventoryDTO> inventory = inventoryService.getLowStockItems();
        return ApiResponse.success(inventory);
    }

    @PostMapping("/low-stock")
    @Operation(summary = "Lấy danh sách hàng sắp hết (POST)",
            description = "Trả về danh sách các mặt hàng có số lượng thấp hơn ngưỡng cảnh báo sử dụng phương thức POST.")
    public ApiResponse<List<InventoryDTO>> getLowStockItemsPost() {
        log.info("REST request to get low stock inventory items (POST)");
        List<InventoryDTO> inventory = inventoryService.getLowStockItems();
        return ApiResponse.success(inventory);
    }

    @GetMapping("/out-of-stock")
    @Operation(summary = "Lấy danh sách hàng đã hết",
            description = "Trả về danh sách các mặt hàng đã hết hàng.")
    public ApiResponse<List<InventoryDTO>> getOutOfStockItems() {
        log.info("REST request to get out of stock inventory items");
        List<InventoryDTO> inventory = inventoryService.getOutOfStockItems();
        return ApiResponse.success(inventory);
    }

    @PostMapping("/out-of-stock")
    @Operation(summary = "Lấy danh sách hàng đã hết (POST)",
            description = "Trả về danh sách các mặt hàng đã hết hàng sử dụng phương thức POST.")
    public ApiResponse<List<InventoryDTO>> getOutOfStockItemsPost() {
        log.info("REST request to get out of stock inventory items (POST)");
        List<InventoryDTO> inventory = inventoryService.getOutOfStockItems();
        return ApiResponse.success(inventory);
    }

    @PatchMapping("/{sku}/quantity/{change}")
    @Operation(summary = "Cập nhật số lượng hàng",
            description = "Cập nhật số lượng của một mặt hàng dựa trên mã SKU.")
    public ApiResponse<InventoryDTO> updateInventoryQuantity(
            @PathVariable String sku,
            @PathVariable Integer change,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to update inventory quantity for SKU: {} by {}", sku, change);
        InventoryDTO inventory = inventoryService.updateInventoryQuantity(sku, change, userDetails.getUsername());
        return ApiResponse.success(inventory);
    }

    @PostMapping("/{sku}/quantity")
    @Operation(summary = "Cập nhật số lượng hàng (POST)",
            description = "Cập nhật số lượng của một mặt hàng dựa trên mã SKU sử dụng phương thức POST.")
    public ApiResponse<InventoryDTO> updateInventoryQuantityPost(
            @PathVariable String sku,
            @RequestParam Integer change,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to update inventory quantity for SKU: {} by {} (POST)", sku, change);
        InventoryDTO inventory = inventoryService.updateInventoryQuantity(sku, change, userDetails.getUsername());
        return ApiResponse.success(inventory);
    }
}
