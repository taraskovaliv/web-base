package dev.kovaliv.view.def;

import j2html.tags.specialized.NavTag;

public abstract class Nav {

    private static GetNav nav;

    public static NavTag getNav(String lang, boolean isAuth) {
        if (nav == null) {
            nav = new GetNav() {
                @Override
                public NavTag nav(String lang, boolean isAuth) {
                    return new NavTag();
                }
            };
        }
        return nav.nav(lang, isAuth);
    }

    public static void setNav(GetNav nav) {
        Nav.nav = nav;
    }
}
