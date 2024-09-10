package dev.kovaliv.services.sitemap;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.getenv;

public class DefaultSitemapService extends AbstractSitemapService {

    @Override
    protected Map<String, Double> getUrls() {
        Map<String, Double> urls = new HashMap<>();
        String hostUri = getenv("HOST_URI");
        urls.put(hostUri, 1.0);
        return urls;
    }
}
