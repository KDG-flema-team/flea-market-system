package com.example.fleamarketsystem.controller.api.v1;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.dto.AdminStatisticsResponse;
import com.example.fleamarketsystem.dto.InfoRequest;
import com.example.fleamarketsystem.dto.InfoResponse;
import com.example.fleamarketsystem.entity.Info;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.AppOrderService;
import com.example.fleamarketsystem.service.InfoService;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('ROLE_read:admin_control')")
public class AdminRestController {

    private final AppOrderService appOrderService;
    private final InfoService infoService;

    public AdminRestController(AppOrderService appOrderService, InfoService infoService) {
        this.appOrderService = appOrderService;
        this.infoService = infoService;
    }

    /*
     * =========================
     * 統計データ（JSON）
     * =========================
     */
    @GetMapping("/statistics")
    public AdminStatisticsResponse getStatistics(

            @LoginUser User AdminUser,

            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        return new AdminStatisticsResponse(
                startDate,
                endDate,
                appOrderService.getTotalSales(startDate, endDate),
                appOrderService.getOrderCountByStatus(startDate, endDate));
    }

    /*
     * =========================
     * 統計 CSV（REST）
     * =========================
     */
    @GetMapping(value = "/statistics/csv", produces = "text/csv")
    public void exportStatisticsCsv(

            @LoginUser User AdminUser,

            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            HttpServletResponse response) throws Exception {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"flea_market_statistics.csv\"");

        PrintWriter writer = response.getWriter();
        appOrderService.writeStatisticsCsv(startDate, endDate, writer);
        writer.flush();
    }

    /*
     * =========================
     * Info管理（CRUD）
     * =========================
     */

    /**
     * Get all info entries
     */
    @GetMapping("/info")
    public ResponseEntity<List<InfoResponse>> getAllInfo(@LoginUser User adminUser) {
        List<InfoResponse> infoList = infoService.getAllInfo().stream()
                .map(InfoResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(infoList);
    }

    /**
     * Get info by ID
     */
    @GetMapping("/info/{id}")
    public ResponseEntity<InfoResponse> getInfoById(
            @LoginUser User adminUser,
            @PathVariable Long id) {
        return infoService.getInfoById(id)
                .map(InfoResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new info entry
     */
    @PostMapping("/info")
    public ResponseEntity<InfoResponse> createInfo(
            @LoginUser User adminUser,
            @Valid @RequestBody InfoRequest request) {
        Info info = new Info();
        info.setTitle(request.title());
        info.setContent(request.content());
        info.setImageUrl(request.imageUrl());
        info.setIsImportant(request.isImportant() != null ? request.isImportant() : false);

        Info createdInfo = infoService.createInfo(info);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InfoResponse.fromEntity(createdInfo));
    }

    /**
     * Update existing info entry
     */
    @PutMapping("/info/{id}")
    public ResponseEntity<InfoResponse> updateInfo(
            @LoginUser User adminUser,
            @PathVariable Long id,
            @Valid @RequestBody InfoRequest request) {
        try {
            Info updatedInfo = new Info();
            updatedInfo.setTitle(request.title());
            updatedInfo.setContent(request.content());
            updatedInfo.setImageUrl(request.imageUrl());
            updatedInfo.setIsImportant(request.isImportant());

            Info savedInfo = infoService.updateInfo(id, updatedInfo);
            return ResponseEntity.ok(InfoResponse.fromEntity(savedInfo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete info entry
     */
    @DeleteMapping("/info/{id}")
    public ResponseEntity<Void> deleteInfo(
            @LoginUser User adminUser,
            @PathVariable Long id) {
        try {
            infoService.deleteInfo(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
