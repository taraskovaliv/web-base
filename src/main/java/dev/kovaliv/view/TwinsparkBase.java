package dev.kovaliv.view;

import io.javalin.http.Context;
import j2html.tags.DomContent;
import j2html.tags.specialized.HtmlTag;
import j2html.tags.specialized.ScriptTag;

import java.util.Arrays;

import static dev.kovaliv.view.Base.getHtml;
import static io.javalin.http.HttpStatus.NOT_ACCEPTABLE;

public class TwinsparkBase {

    public static ScriptTag twinsparkJs() {
        return new ScriptTag().withSrc("https://storage.kovaliv.dev/twinspark.min.js");
    }

    public static void render(Context ctx, Pair pair) {
        if (accepts(ctx, "text/html+partial")) {
            ctx.html(pair.partial);
        } else if (accepts(ctx, "text/html")) {
            ctx.html(getHtml(pair.full));
        } else {
            ctx.status(NOT_ACCEPTABLE);
        }
    }

    public static class Pair {
        HtmlTag full;
        String partial;

        public Pair(HtmlTag full, String partial) {
            this.full = full;
            this.partial = partial;
        }

        public Pair(HtmlTag full, DomContent partial) {
            this.full = full;
            this.partial = partial.render();
        }

        public Pair(HtmlTag full, DomContent[] partial) {
            this.full = full;
            this.partial = Arrays.stream(partial)
                    .map(DomContent::render)
                    .reduce("", String::concat);
        }

        public static Pair of(HtmlTag full, String partial) {
            return new Pair(full, partial);
        }
    }

    private static boolean accepts(Context ctx, String type) {
        String accept = ctx.header("accept");
        return accept != null && accept.startsWith(type);
    }
}
