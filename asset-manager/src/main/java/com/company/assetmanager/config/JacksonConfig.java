package com.company.assetmanager.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Global Jackson ObjectMapper configuration.
 *
 * <p>Default typing is enabled so that abstract return types in API responses
 * (e.g. {@code List<AssetDto>} where the runtime type may be a subclass) are
 * serialised with embedded type metadata.  This allows client SDKs to
 * deserialise polymorphic payloads without extra glue code.
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Embed Java type names in serialised JSON so polymorphic types round-trip
        // correctly.  Required for the abstract AssetDto hierarchy used in reports.
        //
        // NOTE: DefaultTyping.NON_FINAL with As.PROPERTY embeds "@class" in every
        // non-final type, which allows a caller who controls JSON input to supply
        // arbitrary class names.  This is a well-known deserialization gadget vector
        // (related to CVE-2017-7525, CVE-2019-14540, and the broader jackson-databind
        // deserialization CVE family).
        mapper.enableDefaultTyping(DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }
}
