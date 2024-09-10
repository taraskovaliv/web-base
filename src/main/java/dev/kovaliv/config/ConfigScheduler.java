package dev.kovaliv.config;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Log4j2
@Configuration
@EnableScheduling
@ComponentScan("dev.kovaliv")
public class ConfigScheduler implements SchedulingConfigurer {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        return createTaskScheduler();
    }

    static @NotNull ThreadPoolTaskScheduler createTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        scheduler.setThreadGroupName("task");
        scheduler.setThreadNamePrefix("task-");
        scheduler.setErrorHandler(t ->
                log.warn("Uncaught exception in scheduled task: {}", t.getMessage(), t.getCause()));
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
    }
}
