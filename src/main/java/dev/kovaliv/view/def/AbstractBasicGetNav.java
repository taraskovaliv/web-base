package dev.kovaliv.view.def;

import j2html.TagCreator;
import j2html.tags.specialized.LiTag;
import j2html.tags.specialized.NavTag;

import java.util.Map;

import static j2html.TagCreator.*;
import static j2html.TagCreator.a;

public abstract class AbstractBasicGetNav extends GetNav {

    @Override
    public NavTag nav(String lang, boolean isAuth) {
        Logo logo = getLogo(lang);
        Map<String, String> menuItems = getMenuItems(lang, isAuth);
        LiTag[] navItems = new LiTag[menuItems.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : menuItems.entrySet()) {
            navItems[i++] = li(
                    a(entry.getKey()).withHref(entry.getValue()).withStyle("height: 85px; line-height: 85px")
            );
        }
        return TagCreator.nav(
                div(
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

    public abstract Map<String, String> getMenuItems(String lang, boolean isAuth);

    public abstract Logo getLogo(String lang);

    public record Logo(String src, String alt, String width, String height) {
        public Logo {
            if (src == null || alt == null || width == null || height == null) {
                throw new IllegalArgumentException("All fields must be not null");
            }
        }
    }
}
