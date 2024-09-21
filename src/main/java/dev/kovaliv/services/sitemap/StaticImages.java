package dev.kovaliv.services.sitemap;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaticImages {

    public static Set<String> getImagePaths() throws URISyntaxException, IOException {
        URI uri = Objects.requireNonNull(StaticImages.class.getResource("/static/img")).toURI();
        try (Stream<Path> stream = Files.list(Paths.get(uri))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(fileName -> "/img/" + fileName)
                    .collect(Collectors.toSet());
        }
    }
}
