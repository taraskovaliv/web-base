package dev.kovaliv.view;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static dev.kovaliv.view.Base.getHtml;
import static dev.kovaliv.view.BasicPages.getError;
import static dev.kovaliv.view.BasicPages.getSuccess;
import static io.javalin.http.HttpStatus.BAD_REQUEST;
import static java.util.Objects.requireNonNullElse;

public abstract class AbstractRoute implements Routing {

    public static void success(Context ctx, String title, String message) {
        success(ctx, title, message, null, null);
    }

    public static void success(Context ctx, String title, String message, BasicPages.BackButton back) {
        success(ctx, title, message, null, back);
    }

    public static void success(Context ctx, String title, String message, String description, BasicPages.BackButton back) {
        ctx.sessionAttribute("title", title);
        ctx.sessionAttribute("message", message);
        ctx.sessionAttribute("description", description);
        ctx.sessionAttribute("back_path", back != null ? back.path() : null);
        ctx.sessionAttribute("back_text", back != null ? back.text() : null);
        ctx.redirect("/success");
    }

    protected static void success(Context ctx) {
        ctx.html(getHtml(getSuccess(ctx)));
        ctx.sessionAttribute("title", null);
        ctx.sessionAttribute("message", null);
        ctx.sessionAttribute("description", null);
        ctx.sessionAttribute("back_path", null);
        ctx.sessionAttribute("back_text", null);
    }

    public static void error(Context ctx, String title, String error) {
        error(ctx, null, title, error, null);
    }

    public static void error(Context ctx, HttpStatus status, String title, String error) {
        error(ctx, status, title, error, null);
    }

    public static void error(Context ctx, String title, String error, BasicPages.BackButton back) {
        error(ctx, null, title, error, back);
    }

    public static void error(Context ctx, HttpStatus status, String title, String error, BasicPages.BackButton back) {
        ctx.status(requireNonNullElse(status, BAD_REQUEST));
        ctx.sessionAttribute("title", title);
        ctx.sessionAttribute("error", error);
        ctx.sessionAttribute("back_path", back != null ? back.path() : null);
        ctx.sessionAttribute("back_text", back != null ? back.text() : null);
        ctx.redirect("/error");
    }

    protected static void error(Context ctx) {
        ctx.html(getHtml(getError(ctx)));
        ctx.sessionAttribute("title", null);
        ctx.sessionAttribute("error", null);
        ctx.sessionAttribute("back_path", null);
        ctx.sessionAttribute("back_text", null);
    }

    public static String decode(String value) {
        if (value == null) {
            return null;
        }
        return UriUtils.decode(value, StandardCharsets.UTF_8);
    }

    public static Map<String, String> parseParams(String body) {
        body = decode(body);
        Map<String, String> result = new HashMap<>();
        Arrays.stream(body.split("&")).forEach(param -> {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                result.put(keyValue[0].toLowerCase(), keyValue[1]);
            } else if (keyValue.length > 2) {
                result.put(keyValue[0].toLowerCase(), param.replaceAll(keyValue[0] + "=", ""));
            }
        });
        return result;
    }
}
