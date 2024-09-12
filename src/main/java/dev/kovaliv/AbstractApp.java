package dev.kovaliv;

import dev.kovaliv.services.DefaultUserValidation;
import dev.kovaliv.services.UserValidation;
import dev.kovaliv.view.AbstractRoute;
import dev.kovaliv.view.Base;
import dev.kovaliv.view.def.GetNav;
import dev.kovaliv.view.def.Head;
import dev.kovaliv.view.def.Nav;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import j2html.TagCreator;
import j2html.tags.DomContent;
import j2html.tags.specialized.NavTag;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class AbstractApp extends AbstractRoute {

    private final UserValidation userValidation = userValidation();

    public Javalin javalin() {
        Nav.setNav(nav());
        Head.addAdditionalTags(defaultHeadAdditionalTags());
        Base.Page.setUserValidation(userValidation);
        Javalin app = Javalin.create(conf -> conf.staticFiles.add("/static", Location.CLASSPATH))
                .get("/error", AbstractApp::error)
                .get("/success", AbstractApp::success)
                .get("/sitemap.xml", AbstractApp::sitemap);
        addEndpoints(app);
        return app;
    }

    protected GetNav nav() {
        return new GetNav() {
            @Override
            public NavTag nav(String lang, boolean isAuth) {
                return TagCreator.nav();
            }
        };
    }

    protected List<DomContent> defaultHeadAdditionalTags() {
        return List.of();
    }

    protected UserValidation userValidation() {
        return new DefaultUserValidation();
    }

    protected boolean authenticate(Context ctx) {
        return userValidation.authenticate(ctx);
    }

    protected boolean isAuthenticated(Context ctx) {
        return userValidation.isAuthenticated(ctx);
    }

    @SneakyThrows
    private static void sitemap(Context ctx) {
        ctx.result(Files.readAllBytes(Path.of("sitemap.xml")))
                .contentType(ContentType.XML);
    }
}
