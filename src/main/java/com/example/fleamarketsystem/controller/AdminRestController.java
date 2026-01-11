package com.example.fleamarketsystem.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.dto.AdminStatisticsResponse;
import com.example.fleamarketsystem.dto.ItemResponse;
import com.example.fleamarketsystem.service.AppOrderService;
import com.example.fleamarketsystem.service.ItemService;

@RestController
@RequestMapping("api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRestController {
	
	private final ItemService itemService;
    private final AppOrderService appOrderService;
    
    public AdminRestController(ItemService itemService, AppOrderService appOrderService) {
        this.itemService = itemService;
        this.appOrderService = appOrderService;
    }
    
    /**
     * 管理者：商品一覧取得
     */
    @GetMapping("/items")
    public List<ItemResponse> getAllItems() {
        return itemService.getAllItems().stream()
        		.map(item -> new ItemResponse(
        				item.getId(),
        	            item.getName(),
        	            item.getDescription(),
        	            item.getPrice(),
        	            item.getStatus(),
        	            item.getImageUrl(),

        	            item.getCategory() != null ? item.getCategory().getId() : null,
        	            item.getCategory() != null ? item.getCategory().getName() : null,

        	            item.getSeller().getId(),
        	            item.getSeller().getName(),

        	            item.getCreatedAt()
        	        ))
        	        .toList();
    }
    
    /**
     * 管理者：商品削除
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 管理者：売上統計取得
     */
    @GetMapping("/statistics")
    public AdminStatisticsResponse getStatistics(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        return new AdminStatisticsResponse(
            startDate,
            endDate,
            appOrderService.getTotalSales(startDate, endDate),
            appOrderService.getOrderCountByStatus(startDate, endDate)
        );
    }
    
    /**
     * 管理者：統計CSVダウンロード
     */
    @GetMapping(value = "/statistics/csv", produces = "text/csv")
    public void exportStatisticsCsv(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

        HttpServletResponse response
    ) throws IOException {

        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=\"flea_market_statistics.csv\""
        );

        appOrderService.writeStatisticsCsv(startDate, endDate, response.getWriter());
    }


}
