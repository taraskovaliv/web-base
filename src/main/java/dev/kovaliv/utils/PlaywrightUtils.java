package dev.kovaliv.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static java.lang.String.join;
import static java.lang.System.getenv;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.hc.core5.http.io.support.ClassicRequestBuilder.get;

@Log4j2
public class PlaywrightUtils {
    private static final Semaphore semaphore = new Semaphore(20);
    private static final String PLAYWRIGHT_BASE_URL;

    static {
        String playwrightBaseUrl = getenv("PLAYWRIGHT_BASE_URL");
        if (playwrightBaseUrl == null || playwrightBaseUrl.isBlank()) {
            playwrightBaseUrl = "http://192.168.1.5:1510";
        }
        PLAYWRIGHT_BASE_URL = playwrightBaseUrl;
    }

    public static Document getPage(PlaywrightOptions options) throws IOException, ParseException {
        try {
            semaphore.acquire();
            CloseableHttpClient httpClient = HttpClientBuilder.create().disableAutomaticRetries().build();
            CloseableHttpResponse response = httpClient.execute(get(PLAYWRIGHT_BASE_URL + "/" + options.getQuery()).build());
            if (response.getCode() != 200) {
                log.warn("Response for {} has code {}", options.getUrl(), response.getCode());
                throw new RuntimeException("Empty response from Playwright code: " + response.getCode());
            }
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            httpClient.close();
            if (responseString.isBlank()) {
                log.warn("Response is empty for {}", options.getUrl());
                throw new RuntimeException("Empty response from Playwright");
            }
            return Jsoup.parse(responseString);
        } catch (InterruptedException e) {
            log.warn("Playwright semaphore interrupted", e);
            throw new RuntimeException(e);
        } finally {
            semaphore.release();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaywrightOptions {
        private String url = "";
        private Integer width;
        private Integer height;
        private List<String> selectors = new ArrayList<>();
        private List<String> clicks = new ArrayList<>();
        private List<String> removes = new ArrayList<>();

        public PlaywrightOptions(String url) {
            this.url = url;
        }

        public PlaywrightOptions(String url, String selector) {
            this.url = url;
            this.selectors.add(selector);
        }

        public String getQuery() {
            String query = "?url=" + URLEncoder.encode(url, UTF_8);
            if (!selectors.isEmpty()) {
                query += "&selectors=" + URLEncoder.encode(join(";", selectors), UTF_8);
            }
            if (!clicks.isEmpty()) {
                query += "&clicks=" + URLEncoder.encode(join(";", clicks), UTF_8);
            }
            if (!removes.isEmpty()) {
                query += "&removes=" + URLEncoder.encode(join(";", removes), UTF_8);
            }
            if (width != null) {
                query += "&width=" + width;
            }
            if (height != null) {
                query += "&height=" + height;
            }
            return query;
        }
    }
}
