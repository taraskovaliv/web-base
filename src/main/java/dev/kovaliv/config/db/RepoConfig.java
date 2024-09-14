package dev.kovaliv.config.db;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static dev.kovaliv.config.ContextConfig.DB_PROFILE;

@Profile(DB_PROFILE)
@Configuration
@ComponentScan("dev.kovaliv.data")
@EnableJpaRepositories("dev.kovaliv.data")
@Import(OrmConfig.class)
public class RepoConfig {
}
