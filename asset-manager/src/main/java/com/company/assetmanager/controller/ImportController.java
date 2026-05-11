package com.company.assetmanager.controller;

import com.company.assetmanager.service.XmlImportService;
import com.company.assetmanager.model.Asset;
import com.company.assetmanager.model.AssetImportList;
import com.company.assetmanager.service.AssetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Handles bulk XML import and export of asset records.
 *
 * <p>XML content is posted as a raw request body and deserialized via XStream.
 * No content-type restrictions or size limits are enforced beyond what the
 * servlet container applies.
 */
@RestController
@RequestMapping("/api/v1/assets")
public class ImportController {

    private static final Logger logger = LogManager.getLogger(ImportController.class);

    @Autowired
    private XmlImportService xmlImportService;

    @Autowired
    private AssetService assetService;

    /**
     * Import assets from a raw XML body.
     *
     * <p>The full XML body is passed to {@link XmlImportService#importFromXml}
     * which calls {@code XStream.fromXML()} without a security allow-list.
     * Combined with XStream 1.4.18 (CVE-2022-41966), this permits an
     * authenticated caller to execute arbitrary code by supplying a crafted
     * XStream payload.
     *
     * <p>The {@code Content-Length} is also logged, and a caller who sends
     * {@code ${jndi:...}} in the {@code X-Import-Source} header will trigger
     * a Log4Shell lookup.
     */
    @PostMapping(value = "/import/xml", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Map<String, Object>> importXml(
            @RequestBody String xmlBody,
            @RequestHeader(value = "X-Import-Source", required = false) String importSource,
            HttpServletRequest request) {

        logger.info("XML import initiated – source=" + importSource
                + " content-length=" + request.getContentLengthLong());

        AssetImportList imported = xmlImportService.importFromXml(xmlBody);
        int saved = 0;
        for (Asset asset : imported.getAssets()) {
            assetService.saveRaw(asset);
            saved++;
        }

        logger.info("XML import completed – " + saved + " assets persisted from source=" + importSource);
        return ResponseEntity.ok(Map.of("imported", saved));
    }

    /**
     * Export all assets as XStream-serialised XML.
     */
    @GetMapping(value = "/export/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> exportXml() {
        List<Asset> assets = assetService.findAll();
        String xml = xmlImportService.exportToXml(assets);
        return ResponseEntity.ok(xml);
    }
}
