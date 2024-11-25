package dev.kovaliv.view.def;

import dev.kovaliv.view.Base;
import j2html.TagCreator;
import j2html.tags.specialized.LiTag;
import j2html.tags.specialized.NavTag;
import lombok.Getter;

import java.util.Map;

import static j2html.TagCreator.*;

public abstract class AbstractBasicGetNav extends GetNav {

    @Override
    public NavTag nav(String lang, boolean isAuth) {
        Logo logo = getLogo(lang);
        Map<String, String> menuItems = getMenuItems(lang, isAuth);
        LiTag[] navItems = new LiTag[menuItems.size() + (hasLangs() ? 1 : 0)];
        int i = 0;
        for (Map.Entry<String, String> entry : menuItems.entrySet()) {
            navItems[i++] = li(
                    a(entry.getKey()).withHref(entry.getValue()).withStyle("height: 85px; line-height: 85px")
            );
        }
        if (hasLangs()) {
            navItems[i] = getLangMenu(lang);
        }
        return TagCreator.nav(
                div(
                        Base.getSaveLiveBanner(),
                        div(
                                a(
                                        img()
                                                .withSrc(logo.src)
                                                .withAlt(logo.alt)
                                                .withWidth(logo.width)
                                                .withHeight(logo.height)
                                ).withHref("/").withClass("logo")
                        ).withClasses("nav-logo-wrap", "local-scroll"),
                        div(
                                i().withClasses("fa", "fa-bars"),
                                span("en".equals(lang) ? "Menu" : "Меню").withClass("sr-only")
                        ).withClass("mobile-nav").attr("role", "button").withTabindex(0),
                        div(
                                ul(navItems).withClasses("clearlist", "scroll-nav", "local-scroll")
                        ).withClasses("inner-nav", "desktop-nav")
                ).withClasses("full-wrapper", "relative", "clearfix")
        ).withClasses("main-nav", "dark", "transparent", "stick-fixed", "wow-menubar");
    }

    private static LiTag getLangMenu(String lang) {
        return li(
                a(
                        span("en".equals(lang) ? "Eng " : "Укр "),
                        i().withClass("mn-has-sub-icon")
                )
                        .withHref("#")
                        .withClass("mn-has-sub"),
                ul(
                        li(a("Українська").attr("onclick", "addUrlParameter('lang', 'uk')")),
                        li(a("English").attr("onclick", "addUrlParameter('lang', 'en')"))
                ).withClass("mn-sub")
        );
    }

    protected Map<String, String> getMenuItems(String lang, boolean isAuth) {
        return Map.of();
    }

    protected abstract Logo getLogo(String lang);

    protected boolean hasLangs() {
        return false;
    }

    @Getter
    public static class Logo {
        private final String src;
        private final String alt;
        private final String width;
        private final String height;

        public Logo(String src, String alt, String width, String height) {
            if (src == null || alt == null || width == null || height == null) {
                throw new IllegalArgumentException("All fields must be not null");
            }
            this.src = src;
            this.alt = alt;
            this.width = width;
            this.height = height;
        }

        public Logo(String src, String alt) {
            this(src, alt, "376", "74");
        }
    }
}
