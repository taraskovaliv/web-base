package dev.kovaliv.services.sitemap;

import cz.jiripinkas.jsitemapgenerator.robots.RobotsRule;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaticFiles {

    public static Set<String> getImagePaths() throws URISyntaxException, IOException {
        URL resource = StaticFiles.class.getResource("/static/img");
        if (Objects.isNull(resource)) {
            return Set.of();
        }
        URI uri = resource.toURI();
        try (Stream<Path> stream = Files.list(Paths.get(uri))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(fileName -> "/img/" + fileName)
                    .collect(Collectors.toSet());
        }
    }

    public static List<RobotsRule> staticPathsRules() {
        return List.of(
                RobotsRule.builder().userAgentAll().disallow("/webfonts/").build(),
                RobotsRule.builder().userAgentAll().disallow("/css/").build(),
                RobotsRule.builder().userAgentAll().disallow("/js/").build()
        );
    }
}
