package dev.kovaliv.view;

import j2html.tags.DomContent;
import j2html.tags.specialized.InsTag;
import j2html.tags.specialized.ScriptTag;

import static j2html.TagCreator.ins;
import static j2html.TagCreator.script;

public class AdViews {

    public static final String AD_CLIENT = "ca-pub-3029870500249130";

    public enum AdType {
        HORIZONTAL_MEDIA, HORIZONTAL_MULTIPLEX
    }

    public static DomContent[] getWithAd(AdType type, DomContent... content) {
        int length = content.length;
        DomContent[] result = new DomContent[length + 3];
        System.arraycopy(content, 0, result, 0, length);
        result[length] = getGoogleAdScript();
        result[length + 1] = getAd(type);
        result[length + 2] = getPushAdScript();
        return result;
    }

    private static InsTag getAd(AdType type) {
        return switch (type) {
            case HORIZONTAL_MEDIA -> ins().withClass("adsbygoogle")
                    .attr("style", "display:block")
                    .attr("data-ad-client", AD_CLIENT)
                    .attr("data-ad-slot", "7650451464")
                    .attr("data-ad-format", "auto")
                    .attr("data-full-width-responsive", "true");
            case HORIZONTAL_MULTIPLEX -> ins().withClass("adsbygoogle")
                    .attr("style", "display:block")
                    .attr("data-ad-client", AD_CLIENT)
                    .attr("data-ad-slot", "2030760442")
                    .attr("data-ad-format", "autorelaxed");
        };
    }

    private static ScriptTag getPushAdScript() {
        return script().withText("(adsbygoogle = window.adsbygoogle || []).push({});");
    }

    private static ScriptTag getGoogleAdScript() {
        return script()
                .attr("src", "https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=" + AD_CLIENT)
                .attr("crossorigin", "anonymous")
                .attr("async");
    }
}
