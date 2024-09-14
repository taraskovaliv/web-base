package dev.kovaliv.config.db;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static dev.kovaliv.config.ContextConfig.DB_PROFILE;

@Data
@Profile(DB_PROFILE)
@Configuration
public class DatabaseConfig {
    @Value("${dev.kovaliv.jdbc.url}")
    private String jdbcUrl;
    @Value("${dev.kovaliv.jdbc.user}")
    private String user;
    @Value("${dev.kovaliv.jdbc.password}")
    private String password;
    @Value("${dev.kovaliv.db.packages}")
    private String packages;
}
