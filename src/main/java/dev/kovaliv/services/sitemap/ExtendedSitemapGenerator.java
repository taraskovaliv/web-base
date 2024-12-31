package dev.kovaliv.services.sitemap;

import cz.jiripinkas.jsitemapgenerator.generator.SitemapGenerator;

public class ExtendedSitemapGenerator extends SitemapGenerator {

    public ExtendedSitemapGenerator(String baseUrl) {
        super(baseUrl);
    }

    public static ExtendedSitemapGenerator of(String baseUrl) {
        return new ExtendedSitemapGenerator(baseUrl);
    }

    public int size() {
        return urls.size();
    }
}
