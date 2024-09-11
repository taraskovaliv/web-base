package dev.kovaliv;

import dev.kovaliv.services.UserValidation;
import dev.kovaliv.view.Base;
import dev.kovaliv.view.BasicPages;
import dev.kovaliv.view.def.GetNav;
import dev.kovaliv.view.def.Head;
import dev.kovaliv.view.def.Nav;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import j2html.tags.DomContent;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.kovaliv.view.Base.getHtml;
import static dev.kovaliv.view.BasicPages.getError;
import static dev.kovaliv.view.BasicPages.getSuccess;
import static io.javalin.http.HttpStatus.BAD_REQUEST;
import static java.util.Objects.requireNonNullElse;

@Log4j2
public abstract class AbstractApp {

    public Javalin javalin() {
        Nav.setNav(nav());
        Head.addAdditionalTags(defaultHeadAdditionalTags());
        Base.Page.setUserValidation(userValidation());
        Javalin app = Javalin.create(conf -> conf.staticFiles.add("/static", Location.CLASSPATH))
                .get("/error", AbstractApp::error)
                .get("/success", AbstractApp::success)
                .get("/sitemap.xml", AbstractApp::sitemap);
        addEndpoints(app);
        return app;
    }

    abstract void addEndpoints(Javalin app);

    abstract GetNav nav();

    abstract List<DomContent> defaultHeadAdditionalTags();

    abstract UserValidation userValidation();

    @SneakyThrows
    private static void sitemap(Context ctx) {
        ctx.result(Files.readAllBytes(Path.of("sitemap.xml")))
                .contentType(ContentType.XML);
    }

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

    private static void success(Context ctx) {
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

    private static void error(Context ctx) {
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
