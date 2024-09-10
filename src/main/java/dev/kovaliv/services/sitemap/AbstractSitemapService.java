package dev.kovaliv.services.sitemap;

import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Log4j2
public abstract class AbstractSitemapService {

    abstract protected Map<String, Double> getUrls();

    protected String getSitemapFilename() {
        return "sitemap.xml";
    }

    public void createSitemap() {
        try {
            log.debug("Start creating sitemap");
            saveSitemap(createSitemapDocument());
            log.info("Sitemap created");
        } catch (TransformerException | ParserConfigurationException e) {
            log.warn("Error saving sitemap", e);
        }
    }

    private Document createSitemapDocument() throws ParserConfigurationException {
        Document sitemap = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = sitemap.createElement("urlset");
        root.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
        sitemap.appendChild(root);

        for (Map.Entry<String, Double> url : getUrls().entrySet()) {
            Element urlElement = sitemap.createElement("url");
            root.appendChild(urlElement);

            Element loc = sitemap.createElement("loc");
            loc.setTextContent(url.getKey());
            urlElement.appendChild(loc);

            Element lastmod = sitemap.createElement("lastmod");
            lastmod.setTextContent(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            urlElement.appendChild(lastmod);

            Element changefreq = sitemap.createElement("changefreq");
            changefreq.setTextContent("daily");
            urlElement.appendChild(changefreq);

            Element priority = sitemap.createElement("priority");
            priority.setTextContent(String.valueOf(url.getValue()));
            urlElement.appendChild(priority);
        }

        return sitemap;
    }

    private void saveSitemap(Document document) throws TransformerException {
        DOMSource dom = new DOMSource(document);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        StreamResult result = new StreamResult(new File(getSitemapFilename()));
        transformer.transform(dom, result);
    }
}
