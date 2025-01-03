package dev.kovaliv.view;

import dev.kovaliv.services.DefaultUserValidation;
import dev.kovaliv.services.UserValidation;
import dev.kovaliv.view.def.Head;
import dev.kovaliv.view.def.Nav;
import io.javalin.http.Context;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import j2html.tags.specialized.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static dev.kovaliv.view.BasicPages.isLink;
import static dev.kovaliv.view.BasicPages.splitToTagsWithLink;
import static j2html.TagCreator.*;
import static java.lang.System.getenv;
import static java.util.Objects.requireNonNullElse;

public class Base {

    public static String getHtml(HtmlTag htmlTag) {
        return "<!DOCTYPE html>" + htmlTag.render();
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

    public static @NotNull String getDomain() {
        return getenv("HOST_URI").replaceAll("https?://", "");
    }

    @Getter
    @Setter
    public static final class Page {
        private static UserValidation userValidation = new DefaultUserValidation();
        private final String title;
        private final String description;
        private final boolean showToTop;
        private final boolean isAuth;
        private final boolean isMobile;

        public Page(String title, String description, boolean showToTop, Context ctx) {
            this.title = title;
            this.description = description;
            this.showToTop = showToTop;
            this.isAuth = isAuth(ctx);
            this.isMobile = isMobile(ctx);
        }

        public Page(String title, String description, Context ctx) {
            this.title = title;
            this.description = description;
            this.showToTop = false;
            this.isAuth = isAuth(ctx);
            this.isMobile = isMobile(ctx);
        }

        public Page(String title, Context ctx) {
            this.title = title;
            this.description = title;
            this.showToTop = false;
            this.isAuth = isAuth(ctx);
            this.isMobile = isMobile(ctx);
        }

        private boolean isAuth(Context ctx) {
            return userValidation.isAuthenticated(ctx);
        }

        public static boolean isMobile(Context ctx) {
            boolean isMobile = "?1".equals(ctx.header("sec-ch-ua"));
            if (!isMobile) {
                List<String> mobileAgents = List.of("android", "webos", "iphone", "ipad", "ipod", "blackberry", "mobile", "opera mini");
                String userAgent = Optional.ofNullable(ctx.userAgent()).orElse("").toLowerCase();
                for (String mobileAgent : mobileAgents) {
                    if (userAgent.contains(mobileAgent)) {
                        return true;
                    }
                }
            }
            return isMobile;
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
                                a("© Kovaliv 2025").withHref("https://kovaliv.dev")
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
                script().withSrc("/js/params.js"),
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

    public static HrTag divider() {
        return hr().withClasses("mt-0", "mb-0", "white");
    }

    public static ATag getEmail() {
        String email = getenv("EMAIL");
        if (email == null || email.isBlank()) {
            email = "taras@kovaliv.dev";
        }
        return a(email).withHref("mailto:" + email);
    }

    public static ScriptTag chartsJs() {
        return script().withSrc("https://cdn.jsdelivr.net/npm/chart.js");
    }

    public static DivTag getSaveLive() {
        return div(
                getSaveLiveLogo(),
                getSaveLiveButton()
        ).withClass("cba");
    }

    public static ATag getSaveLiveLogo() {
        return a(
                img()
                        .withSrc("/img/save-life-logo.svg")
                        .withAlt("SaveLife - Повернись живим!")
                        .withStyle("width: 97px;height: 48px")
        ).withHref("https://link.kovaliv.dev/savelife").withClass("cba-logo");
    }

    public static ATag getSaveLiveButton() {
        return a(
                span(
                        new SvgTag().withStyle("transform: scale(0.95)")
                                .attr("width", "19")
                                .attr("height", "20")
                                .attr("viewBox", "0 0 19 20")
                                .attr("fill", "none")
                                .attr("xmlns", "http://www.w3.org/2000/svg")
                                .with(new PathTag()
                                        .attr("d", "M16.6159 7.98068L9.25075 17.7431L1.8856 7.98068L1.88557 7.98064C0.522531 6.17413 0.756095 3.66224 2.42693 2.135L2.42702 2.13492C3.33721 1.30274 4.56887 0.898143 5.79348 1.02191L5.79514 1.02207C6.84144 1.12605 7.806 1.60704 8.52511 2.36538L9.25074 3.13058L9.97636 2.36538C10.6946 1.60793 11.667 1.12601 12.7069 1.02201L12.7075 1.02196C13.94 0.898051 15.164 1.30246 16.0745 2.13492L16.076 2.13631C17.7532 3.66341 17.9862 6.17312 16.6173 7.97881L16.6159 7.98068Z")
                                        .attr("stroke", "white")
                                        .attr("stroke-width", "2"))
                ).withClass("icon"),
                span("ПІДТРИМАТИ").withClass("text")
        )
                .withClass("btn-heart")
                .withHref("https://link.kovaliv.dev/savelife_donate");
    }

    public static class PathTag extends ContainerTag<PathTag> {
        public PathTag() {
            super("path");
        }
    }

    public static class SvgTag extends ContainerTag<SvgTag> {
        public SvgTag() {
            super("svg");
        }
    }

    public static class TextTag extends ContainerTag<TextTag> {
        public TextTag() {
            super("text");
        }
    }

    public static DomContent[] content(DomContent[]... contents) {
        List<DomContent> content = new ArrayList<>();
        for (DomContent[] domContents : contents) {
            Collections.addAll(content, domContents);
        }
        return content.toArray(DomContent[]::new);
    }

    public static @NotNull String getLang(Context context) {
        String lang = context.queryParam("lang");
        if (lang == null) {
            lang = context.sessionAttribute("lang");
        } else {
            context.sessionAttribute("lang", lang);
        }
        if (lang == null) {
            AtomicReference<String> acceptLanguage = new AtomicReference<>();
            context.headerMap().forEach((key, value) -> {
                if (key.equalsIgnoreCase("accept-language")) {
                    acceptLanguage.set(value);
                }
            });
            if (acceptLanguage.get() != null && acceptLanguage.get().trim().length() >= 2) {
                lang = acceptLanguage.get().trim().substring(0, 2);
            }
        }
        if (lang == null) {
            lang = "uk";
        }
        return lang;
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

    public static SectionTag getBasePageContainer(DomContent... contents) {
        return pageSection(div(
                div(contents).withClass("row")
        ).withClasses("container", "relative"));
    }

    public static SectionTag pageSection(DomContent... dc) {
        return section(dc).withClasses("page-section", "bg-dark", "light-content");
    }

    public static SectionTag smallSection(DomContent... dc) {
        return section(dc).withClasses("small-section", "bg-dark", "light-content");
    }

    @Deprecated
    public static HtmlTag getPage(String title, DomContent content, Context ctx) {
        return getPage(new Page(title, ctx), ctx, getBasePageContainer(content));
    }

    public static HtmlTag getPage(Page page, Context ctx, DomContent... contents) {
        return getPage(page, ctx, Collections.emptyList(), contents);
    }

    public static HtmlTag getPage(Page page, BasicHeader header, Context ctx, DomContent... contents) {
        return getPage(page, header, ctx, Collections.emptyList(), contents);
    }

    public static HtmlTag getPage(Page page, BasicHeader header, Context ctx, List<? extends DomContent> additionalHeaderTags, DomContent... contents) {
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
        return getPage(page, ctx, additionalHeaderTags, domContents);
    }

    public static HtmlTag getPage(Page page, Context ctx, List<? extends DomContent> additionalHeaderTags, DomContent... contents) {
        String lang = getLang(ctx);
        List<DomContent> contentList = new ArrayList<>();
        List<DomContent> content = new ArrayList<>();
        content.add(Nav.getNav(lang, page.isAuth));
        DomContent[] contentsList = new DomContent[contents.length + 1];
        contentsList[0] = div().withClasses("small-section", "bg-dark-lighter").withStyle("padding-top:100px");
        System.arraycopy(contents, 0, contentsList, 1, contents.length);
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

    public static DivTag getSaveLiveBanner() {
        return div(
                div(
                        a(img()
                                .withSrc("/img/save-life-logo.svg")
                                .withAlt("SaveLife - Повернись живим!")
                                .withStyle("width: 97px;height: 48px")
                        ).withHref("https://link.kovaliv.dev/savelife"),
                        a(img()
                                .withSrc("/img/dronopad.svg")
                                .withAlt("Дронопад - Повернись живим!")
                                .withStyle("max-width: 60%; max-height: 58px")
                        ).withHref("https://link.kovaliv.dev/dronopad"),
                        a(button("MONO Банка")
                                .withClasses("btn", "btn-mod", "btn-glass", "btn-round", "btn-medium")
                                .withStyle("border: none")
                        ).withHref("https://link.kovaliv.dev/mono_dronopad")
                ).withClasses("d-flex", "justify-content-around", "align-items-center")
        ).withStyle("background-color: #9f87e3; text-align: center; width: 100%");
    }
}
