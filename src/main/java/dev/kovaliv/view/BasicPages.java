package dev.kovaliv.view;

import io.javalin.http.Context;
import j2html.tags.DomContent;
import j2html.tags.specialized.DivTag;
import j2html.tags.specialized.HtmlTag;
import j2html.tags.specialized.PTag;
import j2html.tags.specialized.SpanTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static j2html.TagCreator.*;
import static java.util.Objects.requireNonNullElse;

public class BasicPages {

    public static HtmlTag getSuccess(Context ctx) {
        String title = ctx.sessionAttribute("title");
        String message = requireNonNullElse(ctx.sessionAttribute("message"), "Успішно завершено");
        String description = ctx.sessionAttribute("description");
        String back_path = ctx.sessionAttribute("back_path");
        String back_text = ctx.sessionAttribute("back_text");

        DivTag content = div(div(
                        getDescription(description)
                ).withClasses("container", "relative")
        ).withClasses("page-section", "bg-dark", "light-content");

        Base.Page page = new Base.Page(requireNonNullElse(title, "Успішно"), message, false, true, ctx);
        return Base.getPage(page,
                new Base.BasicHeader(title, message, new BackButton(back_path, back_text)),
                ctx, content);
    }

    public static HtmlTag getError(Context ctx) {
        String title = ctx.sessionAttribute("title");
        String error = ctx.sessionAttribute("error");
        String back_path = ctx.sessionAttribute("back_path");
        String back_text = ctx.sessionAttribute("back_text");

        Base.Page page = new Base.Page(requireNonNullElse(title, "Помилка"), error, false, true, ctx);
        return Base.getPage(page,
                new Base.BasicHeader(title, "Помилка: " + error, new BackButton(back_path, back_text)),
                ctx, div().withClasses("page-section", "bg-dark", "light-content"));
    }

    private static PTag getDescription(String text) {
        if (text == null) {
            return null;
        }
        if (isLink(text)) {
            List<DomContent> tags = splitToTagsWithLink(text);
            return p(tags.toArray(DomContent[]::new));
        }
        return p(text);
    }

    public static boolean isLink(String text) {
        if (text == null) {
            return false;
        }
        return text.contains("http://") || text.contains("https://");
    }

    public static @NotNull List<DomContent> splitToTagsWithLink(String text) {
        List<String> links = getLinks(text);
        List<DomContent> tags = new ArrayList<>();
        String[] rows = text.split("\n");
        for (String row : rows) {
            tags.add(span(row.trim()));
            tags.add(br());
        }
        tags.removeLast();
        for (String link : links) {
            List<DomContent> newTags = new ArrayList<>();
            for (DomContent tag : tags) {
                if (tag instanceof SpanTag spanTag) {
                    if (spanTag.render().contains(link)) {
                        String tmp = spanTag.render()
                                .replaceAll("<span>", "")
                                .replaceAll("</span>", "");
                        String[] parts = tmp.split(link);
                        if (parts.length > 0) {
                            newTags.add(span(parts[0]));
                        }
                        if (parts.length > 1) {
                            for (int i = 1; i < parts.length - 1; i++) {
                                newTags.add(a(trimLink(link)).withHref(link));
                                newTags.add(span(parts[i]));
                            }
                        } else {
                            newTags.add(a(trimLink(link)).withHref(link));
                        }
                    } else {
                        newTags.add(spanTag);
                    }
                } else {
                    newTags.add(tag);
                }
            }
            tags = newTags;
        }
        return tags;
    }

    private static String trimLink(String link) {
        link = link.replaceAll("http://", "");
        link = link.replaceAll("https://", "");
        link = link.replaceAll("www.", "");
        return link;
    }

    private static List<String> getLinks(String text) {
        List<String> links = new ArrayList<>();
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (isLink(word)) {
                links.add(word);
            }
        }
        return links;
    }

    public static BackButton HOME_BUTTON = new BackButton("/", "На головну");

    public record BackButton(String path, String text) {
    }
}
