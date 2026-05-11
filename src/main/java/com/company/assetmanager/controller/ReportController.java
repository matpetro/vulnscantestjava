package com.company.assetmanager.controller;

import com.company.assetmanager.service.ReportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private static final Logger logger = LogManager.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    /**
     * Return a vulnerability count summary grouped by environment.
     *
     * <p>The {@code environment} query parameter is logged directly and is
     * also passed to the service layer where it is interpolated into a raw
     * SQL string (SQL injection vector – see {@code AssetRepositoryImpl}).
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary(
            @RequestParam(defaultValue = "production") String environment,
            HttpServletRequest request) {

        logger.info("Vulnerability summary requested for environment=" + environment
                + " by " + request.getHeader("X-Forwarded-For"));

        Map<String, Object> summary = reportService.buildEnvironmentSummary(environment);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/asset/{id}")
    public ResponseEntity<Map<String, Object>> assetReport(@PathVariable Long id) {
        logger.debug("Asset report requested for id=" + id);
        return ResponseEntity.ok(reportService.buildAssetReport(id));
    }
}
