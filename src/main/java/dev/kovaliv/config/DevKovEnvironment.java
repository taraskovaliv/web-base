package dev.kovaliv.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

import static java.lang.System.getenv;

@Log4j2
public class DevKovEnvironment extends StandardEnvironment {
    public static final String DEFAULT_PROPERTIES_PATH = "application.properties";

    @Override
    protected void customizePropertySources(MutablePropertySources propertySources) {
        try {
            propertySources.addFirst(getProperties());
        } catch (IOException e) {
            log.warn("DEV KOV Properties file not found");
        }
        super.customizePropertySources(propertySources);
    }

    private PropertySource<?> getProperties() throws IOException {
        String path = getenv("DEV_KOV_PROPERTIES_PATH");
        if (path == null || path.isBlank()) {
            path = DEFAULT_PROPERTIES_PATH;
        }
        return new ResourcePropertySource("dev_kov_resource", new ClassPathResource(path));
    }
}
