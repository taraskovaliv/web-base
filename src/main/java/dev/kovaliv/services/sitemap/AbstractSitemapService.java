package dev.kovaliv.services.sitemap;

import cz.jiripinkas.jsitemapgenerator.ChangeFreq;
import cz.jiripinkas.jsitemapgenerator.Ping;
import cz.jiripinkas.jsitemapgenerator.WebPage;
import cz.jiripinkas.jsitemapgenerator.generator.SitemapGenerator;
import cz.jiripinkas.jsitemapgenerator.robots.RobotsRule;
import cz.jiripinkas.jsitemapgenerator.robots.RobotsTxtGenerator;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;

import static cz.jiripinkas.jsitemapgenerator.Ping.SearchEngine.BING;
import static cz.jiripinkas.jsitemapgenerator.Ping.SearchEngine.GOOGLE;
import static dev.kovaliv.services.sitemap.StaticFiles.getImagePaths;
import static dev.kovaliv.services.sitemap.StaticFiles.staticPathsRules;
import static java.lang.System.getenv;

@Log4j2
public abstract class AbstractSitemapService {

    @Scheduled(cron = "0 0 4 * * *")
    @SchedulerLock(name = "sitemap-robots-txt-creation", lockAtMostFor = "PT60S")
    public void createSitemapAndRobotTxt() {
        createSitemap();
        createRobotTxt();
    }

    public void createSitemap() {
        try {
            log.debug("Start creating sitemap");
            createSitemapAndPing();
            log.info("Sitemap created");
        } catch (IOException | URISyntaxException e) {
            log.warn("Error saving sitemap", e);
        }
    }

    public void createRobotTxt() {
        try {
            log.debug("Start creating robots.txt");
            createRobotTxtFile();
            log.info("robots.txt created");
        } catch (IOException e) {
            log.warn("Error saving robots.txt", e);
        }
    }

    private void createRobotTxtFile() throws FileNotFoundException {
        RobotsTxtGenerator robotsTxtGenerator = RobotsTxtGenerator.of(getenv("HOST_URI"));
        robotsTxtGenerator.addSitemap(getSitemapFilename());
        robotsTxtGenerator.addRule(RobotsRule.builder().userAgentAll().allowAll().build());
        staticPathsRules().forEach(robotsTxtGenerator::addRule);
        disallowPaths().forEach(path -> robotsTxtGenerator.addRule(
                RobotsRule.builder().userAgentAll().disallow(path).build()
        ));

        PrintWriter writer = new PrintWriter("robots.txt");
        Arrays.stream(robotsTxtGenerator.constructRobotsTxt()).forEach(writer::println);
        writer.close();
    }

    private void createSitemapAndPing() throws IOException, URISyntaxException {
        String hostUri = getenv("HOST_URI");
        if (hostUri == null || hostUri.isBlank()) {
            return;
        }

        SitemapGenerator sitemapGenerator = SitemapGenerator.of(hostUri)
                .addPage(WebPage.builder().maxPriorityRoot().changeFreqNever().lastModNow().build());
        getImagePaths().forEach(path -> sitemapGenerator.addPage(WebPage.builder()
                .name(path)
                .priority(0.7)
                .changeFreq(ChangeFreq.WEEKLY)
                .lastModNow()
                .build()));

        for (Map.Entry<String, SMValue> smvalue : getUrls().entrySet()) {
            try {
                String url = smvalue.getKey();
                if (!url.startsWith(hostUri)) {
                    url = hostUri + url;
                }
                new URI(url);
            } catch (URISyntaxException e) {
                log.warn("Invalid URL: {}", smvalue.getKey());
                continue;
            }

            sitemapGenerator.addPage(WebPage.builder()
                    .name(smvalue.getKey())
                    .priority(smvalue.getValue().getPriority())
                    .changeFreq(smvalue.getValue().getFreq())
                    .lastModNow()
                    .build());
        }

        sitemapGenerator.toFile(Paths.get(getSitemapFilename()));

        new Thread(() -> {
            Ping ping = Ping.builder()
                    .engines(GOOGLE, BING)
                    .build();
            sitemapGenerator
                    .ping(ping)
                    .callOnSuccess(() -> log.info("Pinged Google and Bing!"))
                    .catchOnFailure(e ->
                            log.warn("Could not ping Google and Bing: {}", e.getMessage())
                    );
        }).start();
    }

    abstract protected Map<String, SMValue> getUrls();

    protected List<String> disallowPaths() {
        return new ArrayList<>();
    }

    protected String getSitemapFilename() {
        return "sitemap.xml";
    }

    @Getter
    public static class SMValue {
        private final ChangeFreq freq;
        private final double priority;

        public SMValue(double priority) {
            this.priority = priority;
            this.freq = ChangeFreq.DAILY;
        }

        public SMValue(double priority, ChangeFreq freq) {
            this.priority = priority;
            this.freq = freq;
        }
    }

    public static class DefaultSitemapService extends AbstractSitemapService {
        @Override
        protected Map<String, SMValue> getUrls() {
            return new HashMap<>();
        }
    }
}
