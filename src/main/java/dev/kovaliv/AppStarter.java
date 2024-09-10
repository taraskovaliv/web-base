package dev.kovaliv;

import dev.kovaliv.services.sitemap.AbstractSitemapService;
import io.javalin.Javalin;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.time.LocalDateTime;

import static dev.kovaliv.config.ContextConfig.context;
import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;
import static java.time.LocalDateTime.now;

@Log4j2
public class AppStarter {

    public static void start(AbstractApp app) {
        start(app, 8080);
    }

    public static void start(AbstractApp app, Integer defaultPort) {
        LocalDateTime start = now();
        Javalin javalin = app.javalin();
        javalin.start(getenv("PORT") != null ? parseInt(getenv("PORT")) : defaultPort);
        log.info("App started in {} seconds", Duration.between(start, now()).getSeconds());
        start = now();
        boolean contextStarted = false;
        int retries = 0;
        while (!contextStarted && retries < 10) {
            try {
                context();
                contextStarted = true;
                log.info("Context started in {} seconds", Duration.between(start, now()).getSeconds());
            } catch (Exception e) {
                retries++;
                log.warn("Context not started yet: {}", e.getMessage(), e);
            }
        }
        if (!contextStarted) {
            log.error("Context not started");
            javalin.stop();
            System.exit(1);
        }
        context().getBean(AbstractSitemapService.class).createSitemap();
    }
}
