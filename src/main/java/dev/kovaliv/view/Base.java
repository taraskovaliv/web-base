package dev.kovaliv.view;

import dev.kovaliv.services.UserValidation;
import dev.kovaliv.view.def.Head;
import dev.kovaliv.view.def.Nav;
import io.javalin.http.Context;
import j2html.tags.DomContent;
import j2html.tags.specialized.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static dev.kovaliv.view.BasicPages.isLink;
import static dev.kovaliv.view.BasicPages.splitToTagsWithLink;
import static j2html.TagCreator.*;
import static java.lang.System.getenv;
import static java.util.Objects.requireNonNullElse;

public class Base {

    public static String getHtml(HtmlTag htmlTag) {
        return getDocType() + htmlTag.render();
    }

    public static String getDocType() {
        return "<!DOCTYPE html>";
    }

    public static HeadTag getHead(Page page, List<? extends DomContent> additionalTags) {
        List<DomContent> tags = new ArrayList<>(List.of(
                title(page.title),
                meta().withName("description").withContent(page.description),

                meta().withCharset("UTF-8"),
                meta().withName("viewport").withContent("width=device-width, initial-scale=1.0"),

                link().withRel("shortcut icon").withType("image/x-icon").withHref("/img/favicon.ico"),

                link().withRel("stylesheet").withHref("/css/bootstrap.min.css"),
                link().withRel("stylesheet").withHref("/css/style.css"),
                link().withRel("stylesheet").withHref("/css/style-responsive.css"),
                link().withRel("stylesheet").withHref("/css/vertical-rhythm.min.css"),
                link().withRel("stylesheet").withHref("/css/owl.carousel.css"),
                link().withRel("stylesheet").withHref("/css/animate.min.css"),
                link().withRel("stylesheet").withHref("/css/splitting.css"),
                script().withSrc("https://plausible.kovaliv.dev/js/script.js")
                        .attr("defer")
                        .attr("data-domain", getDomain())
        ));
        tags.addAll(Head.getAdditionalTags());
        if (additionalTags != null) {
            tags.addAll(additionalTags);
        }
        return head(tags.toArray(DomContent[]::new));
    }

    private static @NotNull String getDomain() {
        return getenv("HOST_URI").replaceAll("https?://", "");
    }

    @Getter
    @Setter
    public static final class Page {
        private static UserValidation userValidation = ctx -> false;
        private final String title;
        private final String description;
        private final boolean showToTop;
        private final boolean bottomMargin;
        private final boolean isAuth;

        public Page(String title, String description, boolean showToTop, boolean bottomMargin, Context ctx) {
            this.title = title;
            this.description = description;
            this.showToTop = showToTop;
            this.bottomMargin = bottomMargin;
            this.isAuth = isAuth(ctx);
        }

        private boolean isAuth(Context ctx) {
            return userValidation.validate(ctx);
        }

        public static void setUserValidation(UserValidation userValidation) {
            Page.userValidation = userValidation;
        }
    }

    public record BasicHeader(String title, String message, BasicPages.BackButton button) {
    }

    public static FooterTag getFooter(boolean showToTop) {
        DomContent[] contents = new DomContent[showToTop ? 2 : 1];
        contents[0] = div(
                div(
                        div(
                                a("© Kovaliv 2024").withHref("https://kovaliv.dev")
                        ).withClass("footer-copy")
                ).withClass("footer-text")
        ).withClass("container");
        if (showToTop) {
            contents[1] = div(
                    a(
                            i().withClass("link-to-top-icon"),
                            span("Scroll to top").withClass("sr-only")
                    )
                            .withHref("#top")
                            .withClass("link-to-top")
            ).withClass("local-scroll");
        }
        return footer(
                contents
        ).withClasses("page-section", "bg-dark-lighter", "light-content", "footer", "pb-100", "pb-sm-50");
    }

    public static List<ScriptTag> getScripts() {
        return List.of(
                script().withSrc("/js/jquery.min.js"),
                script().withSrc("/js/jquery.easing.1.3.js"),
                script().withSrc("/js/bootstrap.bundle.min.js"),
                script().withSrc("/js/SmoothScroll.js"),
                script().withSrc("/js/jquery.scrollTo.min.js"),
                script().withSrc("/js/jquery.localScroll.min.js"),
                script().withSrc("/js/jquery.viewport.mini.js"),
                script().withSrc("/js/jquery.parallax-1.1.3.js"),
                script().withSrc("/js/owl.carousel.min.js"),
                script().withSrc("/js/isotope.pkgd.min.js"),
                script().withSrc("/js/imagesloaded.pkgd.min.js"),
                script().withSrc("/js/masonry.pkgd.min.js"),
                script().withSrc("/js/jquery.lazyload.min.js"),
                script().withSrc("/js/wow.min.js"),
                script().withSrc("/js/morphext.js"),
                script().withSrc("/js/typed.min.js"),
                script().withSrc("/js/all.js"),
                script().withSrc("/js/jquery.ajaxchimp.min.js"),
                script().withSrc("/js/objectFitPolyfill.min.js"),
                script().withSrc("/js/splitting.min.js"),
                script().withSrc("/js/gsap.min.js")
        );
    }

    private static DivTag getLoader(String lang) {
        String loadingText = "Завантаження...";
        if ("en".equals(lang)) {
            loadingText = "Loading...";
        }
        return div(
                div(loadingText).withClass("loader")
        ).withClasses("page-loader", "dark");
    }

    public static ATag getEmail() {
        String email = getenv("EMAIL");
        if (email == null || email.isBlank()) {
            email = "taras@kovaliv.dev";
        }
        return a(email).withHref("mailto:" + email);
    }

    private static PTag getMessage(String text) {
        if (text == null) {
            return null;
        }
        if (isLink(text)) {
            List<DomContent> tags = splitToTagsWithLink(text);
            return p(tags.toArray(DomContent[]::new)).withClasses("hs-line-6", "opacity-075", "mb-20", "mb-xs-0");
        }
        return p(text).withClasses("hs-line-6", "opacity-075", "mb-20", "mb-xs-0");
    }

    public static HtmlTag getPage(Page page, DomContent... contents) {
        return getPage(page, "uk", Collections.emptyList(), contents);
    }

    public static HtmlTag getPage(Page page, BasicHeader header, DomContent... contents) {
        DomContent[] heading = new DomContent[(header.button != null && header.button.path() != null && !header.button.path().isBlank()) ? 2 : 1];
        heading[0] = div(
                div(
                        h1(
                                requireNonNullElse(header.title, "Успішно")
                        ).withClasses("hs-line-7", "mb-20", "mb-xs-10")
                ).withClasses("wow", "fadeInUpShort")
                        .attr("data-wow-delay", ".1s"),

                div(
                        getMessage(header.message)
                ).withClasses("wow", "fadeInUpShort")
                        .attr("data-wow-delay", ".2s")
        ).withClass("col-md-8");
        if (header.button != null && header.button.path() != null && !header.button.path().isBlank()) {
            heading[1] = div(
                    a(
                            requireNonNullElse(header.button.text(), "Назад")
                    ).withHref(header.button.path())
                            .withClasses("btn", "btn-mod", "btn-border-w", "btn-round", "btn-medium")
            )
                    .attr("data-wow-delay", ".1s")
                    .withClasses("col-md-4", "mt-30", "wow", "fadeInUpShort");
        }

        DomContent[] domContents = new DomContent[1 + contents.length];
        domContents[0] = div(
                div(
                        div(
                                heading
                        ).withClass("row")
                ).withClasses("container", "relative")
        ).withClasses("small-section", "bg-dark-lighter")
                .withStyle("padding:0")
                .withId("home");
        System.arraycopy(contents, 0, domContents, 1, contents.length);
        return getPage(page, "uk", Collections.emptyList(), domContents);
    }

    public static HtmlTag getPage(Page page, String lang, List<? extends DomContent> additionalHeaderTags, DomContent... contents) {
        if (lang == null) {
            lang = "uk";
        }
        List<DomContent> contentList = new ArrayList<>();
        List<DomContent> content = new ArrayList<>();
        content.add(Nav.getNav(lang, page.isAuth));
        DomContent[] contentsList = new DomContent[contents.length + (page.bottomMargin ? 1 : 0)];
        if (page.bottomMargin) {
            contentsList[0] = div().withClasses("small-section", "bg-dark-lighter");
        }
        System.arraycopy(contents, 0, contentsList, page.bottomMargin ? 1 : 0, contents.length);
        content.add(main(contentsList).withId("main"));
        content.add(getFooter(page.showToTop));
        contentList.add(getLoader(lang));
        contentList.add(a("Skip to Content").withClasses("btn", "skip-to-content").withHref("#main"));
        contentList.add(div(
                content.toArray(DomContent[]::new)
        ).withId("top").withClasses("page", "bg-dark", "light-content"));
        contentList.addAll(getScripts());
        return html(
                getHead(page, additionalHeaderTags),
                body(
                        contentList.toArray(DomContent[]::new)
                ).withClass("appear-animate")
                        .withStyle("background-color:rgb(35, 35, 35)")
        ).withLang(lang);
    }
}
