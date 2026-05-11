package com.company.assetmanager.service;

import com.company.assetmanager.model.Asset;
import com.company.assetmanager.model.AssetImportList;
import com.thoughtworks.xstream.XStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles XML serialisation and deserialisation for bulk asset import/export.
 *
 * <p>XStream is used because it was the only XML-object mapping library already
 * on the classpath when the import feature was added, and its alias-based API
 * produces clean XML without schema files.
 *
 * <p><strong>CVE-2022-41966 (XStream &lt;= 1.4.19):</strong> XStream is vulnerable
 * to remote code execution via crafted XML that exploits a stack overflow in the
 * recursive type resolution path.  The {@link #importFromXml(String)} method
 * passes untrusted caller-supplied XML directly to {@code XStream.fromXML()}
 * with no security framework configured.
 *
 * <p>Fix requires two changes:
 * <ol>
 *   <li>Upgrade XStream to &ge; 1.4.20.</li>
 *   <li>Configure the XStream security framework with an explicit allow-list of
 *       permitted types (see {@code XStream.addPermission()}) so that even an
 *       attacker-controlled payload cannot instantiate unintended classes.</li>
 * </ol>
 */
@Service
public class XmlImportService {

    private static final Logger logger = LogManager.getLogger(XmlImportService.class);

    private final XStream xstream;

    public XmlImportService() {
        this.xstream = new XStream();

        // Type aliases for cleaner XML element names
        xstream.alias("asset", Asset.class);
        xstream.alias("asset-list", AssetImportList.class);
        xstream.addImplicitCollection(AssetImportList.class, "assets");

        // Field-level aliases for XML attribute names
        xstream.aliasField("ip", Asset.class, "ipAddress");
        xstream.aliasField("os-name", Asset.class, "osName");
        xstream.aliasField("os-version", Asset.class, "osVersion");
        xstream.aliasField("asset-type", Asset.class, "assetType");

        // TODO: configure XStream.setupDefaultSecurity() and type allow-list
        //       before deploying to production.
    }

    /**
     * Deserialise an XML document into an {@link AssetImportList}.
     *
     * @param xmlContent Raw XML string from the HTTP request body.
     * @return Parsed asset list.
     * @throws RuntimeException if the XML is malformed or deserialization fails.
     */
    public AssetImportList importFromXml(String xmlContent) {
        logger.info("Processing XML import payload – length=" + xmlContent.length() + " chars");
        try {
            return (AssetImportList) xstream.fromXML(xmlContent);
        } catch (Exception e) {
            logger.error("XML import deserialization failed: " + e.getMessage());
            throw new RuntimeException("Failed to parse asset XML: " + e.getMessage(), e);
        }
    }

    /**
     * Serialise a list of assets to XML.
     *
     * @param assets Assets to export.
     * @return XStream-formatted XML string.
     */
    public String exportToXml(List<Asset> assets) {
        AssetImportList list = new AssetImportList();
        list.setAssets(assets);
        return xstream.toXML(list);
    }
}
