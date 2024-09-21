package dev.kovaliv.services.sitemap;

import cz.jiripinkas.jsitemapgenerator.robots.RobotsRule;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class StaticFiles {

    public static Set<String> getImagePaths() {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL url = loader.getResource("static/img");
            if (url != null) {
                File[] files = new File(url.getPath()).listFiles();
                if (files != null) {
                    return Arrays.stream(files)
                            .filter(file -> !Files.isDirectory(file.toPath()))
                            .map(File::getName)
                            .map(fileName -> "/img/" + fileName)
                            .collect(Collectors.toSet());
                }
            }
        } catch (Exception e) {
            log.warn("Error getting image paths", e);
        }
        return Set.of();
    }

    public static List<RobotsRule> staticPathsRules() {
        return List.of(
                RobotsRule.builder().userAgentAll().disallow("/webfonts/").build(),
                RobotsRule.builder().userAgentAll().disallow("/css/").build(),
                RobotsRule.builder().userAgentAll().disallow("/js/").build()
        );
    }
}
