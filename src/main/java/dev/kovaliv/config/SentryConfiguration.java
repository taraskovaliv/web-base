package dev.kovaliv.config;

import io.sentry.spring.jakarta.EnableSentry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static dev.kovaliv.config.ContextConfig.SENTRY_PROFILE;

@Profile(SENTRY_PROFILE)
@Configuration
@EnableSentry(dsn = "${sentry.dns}")
public class SentryConfiguration {
}
