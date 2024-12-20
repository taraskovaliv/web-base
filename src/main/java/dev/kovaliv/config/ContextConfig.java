package dev.kovaliv.config;

import dev.kovaliv.services.sitemap.AbstractSitemapService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Log4j2
public class ContextConfig extends StandardEnvironment {
    public static final String DB_PROFILE = "db";
    public static final String SCHEDULER_PROFILE = "scheduler";
    public static final String SENTRY_PROFILE = "sentry";
    public static final String REDIS_PROFILE = "redis";
    public static final String SLACK_PROFILE = "slack";
    public static final String AI_PROFILE = "ai";
    public static final String R2_PROFILE = "r2";

    public static final String DEFAULT_TIMEZONE = "Kyiv/Europe";

    private static AnnotationConfigApplicationContext context;
    private static ConfigurableEnvironment env;

    public synchronized static AnnotationConfigApplicationContext context() {
        if (context == null) {
            TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
            context = new AnnotationConfigApplicationContext();
            context.setEnvironment(new DevKovEnvironment());
            addProfiles();
            context.scan("dev.kovaliv");
            context.refresh();
            addDefaultBeans();
        }
        return context;
    }

    public synchronized static ConfigurableEnvironment env() {
        if (env == null) {
            env = context().getEnvironment();
        }
        return env;
    }

    public static boolean isCreatedContext() {
        return context != null;
    }

    public static <T> Optional<T> get(Class<T> clazz) {
        if (context == null) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(context.getBean(clazz));
            } catch (NoSuchBeanDefinitionException e) {
                return Optional.empty();
            }
        }
    }

    private static void addProfiles() {
        if (isDb(context)) {
            log.info("DB profile is enabled");
            context.getEnvironment().addActiveProfile(DB_PROFILE);
        }
        if (isScheduler(context)) {
            log.info("Scheduler profile is enabled");
            context.getEnvironment().addActiveProfile(SCHEDULER_PROFILE);
        }
        if (isSentry(context)) {
            log.info("Sentry profile is enabled");
            context.getEnvironment().addActiveProfile(SENTRY_PROFILE);
        }
        if (isRedis(context)) {
            log.info("Redis profile is enabled");
            context.getEnvironment().addActiveProfile(REDIS_PROFILE);
        }
        if (isSlack(context)) {
            log.info("Slack profile is enabled");
            context.getEnvironment().addActiveProfile(SLACK_PROFILE);
        }
        if (isAI(context)) {
            log.info("AI profile is enabled");
            context.getEnvironment().addActiveProfile(AI_PROFILE);
        }
        if (isR2(context)) {
            log.info("R2 profile is enabled");
            context.getEnvironment().addActiveProfile(R2_PROFILE);
        }
    }

    private static void addDefaultBeans() {
        try {
            context.getBean(AbstractSitemapService.class);
        } catch (NoSuchBeanDefinitionException e) {
            context.registerBean(AbstractSitemapService.DefaultSitemapService.class);
        }
    }

    private static boolean isScheduler(AnnotationConfigApplicationContext context) {
        return "true".equals(context.getEnvironment().getProperty("dev.kovaliv.scheduler.enable"));
    }

    private static boolean isDb(AnnotationConfigApplicationContext context) {
        List<String> properties = List.of(
                "dev.kovaliv.jdbc.url", "dev.kovaliv.jdbc.user",
                "dev.kovaliv.jdbc.password", "dev.kovaliv.db.packages"
        );
        for (String property : properties) {
            if (!propertyIsNotBlank(context, property)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSentry(AnnotationConfigApplicationContext context) {
        return propertyIsNotBlank(context, "sentry.dsn");
    }

    private static boolean isRedis(AnnotationConfigApplicationContext context) {
        List<String> properties = List.of("REDIS_HOST", "REDIS_PASSWORD");
        for (String property : properties) {
            if (!propertyIsNotBlank(context, property)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSlack(AnnotationConfigApplicationContext context) {
        return propertyIsNotBlank(context, "slack.token");
    }

    private static boolean isAI(AnnotationConfigApplicationContext context) {
        return propertyIsNotBlank(context, "CLOUDFLARE_ACCOUNT_ID")
                && propertyIsNotBlank(context, "CLOUDFLARE_AUTH_TOKEN");
    }

    private static boolean isR2(AnnotationConfigApplicationContext context) {
        return propertyIsNotBlank(context, "R2_ACCOUNT_ID")
                && propertyIsNotBlank(context, "R2_ACCESS_KEY")
                && propertyIsNotBlank(context, "R2_SECRET_KEY");
    }

    private static boolean propertyIsNotBlank(AnnotationConfigApplicationContext context, String propertyName) {
        String property = context.getEnvironment().getProperty(propertyName);
        return property != null && !property.isBlank();
    }
}
