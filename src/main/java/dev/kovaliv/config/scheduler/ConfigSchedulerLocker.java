package dev.kovaliv.config.scheduler;

import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

import static dev.kovaliv.config.ContextConfig.DB_PROFILE;
import static dev.kovaliv.config.ContextConfig.SCHEDULER_PROFILE;

@Log4j2
@Profile({DB_PROFILE, SCHEDULER_PROFILE})
@Configuration
@EnableSchedulerLock(defaultLockAtLeastFor = "PT30S", defaultLockAtMostFor = "PT55S")
public class ConfigSchedulerLocker {

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        log.debug("Creating lock provider");
        createTableIfNotExists(dataSource);
        return new JdbcTemplateLockProvider(dataSource);
    }

    private void createTableIfNotExists(DataSource dataSource) {
        ResourceDatabasePopulator sqlExecutor = new ResourceDatabasePopulator();
        sqlExecutor.addScript(new ClassPathResource("sql/createShedlock.sql"));
        sqlExecutor.execute(dataSource);
    }
}
