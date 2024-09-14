package dev.kovaliv.config.scheduler;

import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

import static dev.kovaliv.config.ContextConfig.*;

@Log4j2
@Profile(SCHEDULER_PROFILE)
@Configuration(enforceUniqueMethods = false)
@EnableScheduling
@ComponentScan("dev.kovaliv")
public class ConfigScheduler {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(ErrorHandler errorHandler) {
        ThreadPoolTaskScheduler taskScheduler = getTaskScheduler();
        taskScheduler.setErrorHandler(errorHandler);
        return taskScheduler;
    }

    @NotNull
    private static ThreadPoolTaskScheduler getTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        scheduler.setThreadGroupName("task");
        scheduler.setThreadNamePrefix("task-");
        scheduler.setErrorHandler(t -> context().getBean(ErrorHandler.class).handleError(t));
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }

    @Bean
    @Profile("!" + SENTRY_PROFILE)
    public ErrorHandler errorHandler() {
        return t ->
                log.warn("Uncaught exception in scheduled task: {}", t.getMessage(), t.getCause());
    }

    @Bean
    @Profile(SENTRY_PROFILE)
    public ErrorHandler errorHandlerWithSentry() {
        return t -> {
            log.warn("Uncaught exception in scheduled task: {}", t.getMessage(), t.getCause());
            Sentry.captureException(t);
        };
    }
}
