package dev.kovaliv.config;

import dev.kovaliv.services.sitemap.AbstractSitemapService;
import dev.kovaliv.services.sitemap.DefaultSitemapService;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.TimeZone;

public class ContextConfig {

    public static final String DEFAULT_TIMEZONE = "Kyiv/Europe";
    private static AnnotationConfigApplicationContext context;

    public synchronized static ApplicationContext context() {
        if (context == null) {
            TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
            context = new AnnotationConfigApplicationContext("dev.kovaliv");
            addDefaultBeans();
        }
        return context;
    }

    private static void addDefaultBeans() {
        try {
            context.getBean(AbstractSitemapService.class);
        } catch (NoSuchBeanDefinitionException e) {
            context.registerBean(DefaultSitemapService.class);
        }
    }
}
