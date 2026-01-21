package com.example.fleamarketsystem.controller.api.v1;

import java.io.PrintWriter;
import java.time.LocalDate;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.dto.AdminStatisticsResponse;
import com.example.fleamarketsystem.service.AppOrderService;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRestController {

    private final AppOrderService appOrderService;

    public AdminRestController(AppOrderService appOrderService) {
        this.appOrderService = appOrderService;
    }

    /* =========================
       統計データ（JSON）
       ========================= */
    @GetMapping("/statistics")
    public AdminStatisticsResponse getStatistics(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
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
                appOrderService.getOrderCountByStatus(startDate, endDate)
        );
    }

    /* =========================
       統計 CSV（REST）
       ========================= */
    @GetMapping(value = "/statistics/csv", produces = "text/csv")
    public void exportStatisticsCsv(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            HttpServletResponse response
    ) throws Exception {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"flea_market_statistics.csv\""
        );

        PrintWriter writer = response.getWriter();
        appOrderService.writeStatisticsCsv(startDate, endDate, writer);
        writer.flush();
    }
}

