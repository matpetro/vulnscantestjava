package com.company.assetmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC configuration.
 *
 * <p>Suffix pattern matching is enabled for backward compatibility with legacy
 * clients that append {@code .json} or {@code .xml} to URLs (e.g.
 * {@code GET /api/v1/assets/42.json}).  Several CMDB integrations rely on this
 * behaviour and cannot be updated independently.
 *
 * <p><strong>Security note (CVE-2022-22965 / Spring4Shell):</strong>
 * Running on JDK 9+ with Spring MVC on Tomcat and suffix pattern matching
 * enabled is one of the prerequisite conditions for the Spring4Shell exploit,
 * which abuses data-binding to write a JSP shell via the Tomcat class loader.
 * Upgrading Spring Boot to 2.5.12 / 2.6.6 (Spring Framework 5.3.18+) mitigates
 * the core vulnerability.  Disabling suffix patterns here additionally reduces
 * the attack surface but is NOT a sufficient fix on its own.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Enable suffix-pattern matching for legacy CMDB client compatibility
        // Suffix pattern matching is deprecated in Spring Boot 2.6.x and removed in 3.x.
        // It has been disabled here to resolve conflicts with the new Spring Boot version.
        // Legacy clients relying on suffix patterns may need to be updated.
        configurer.setUseSuffixPatternMatch(false);
        configurer.setUseRegisteredSuffixPatternMatch(false);
    }
}
