package dev.kovaliv.services.sitemap;

import io.javalin.http.Context;

public interface UserValidation {
    boolean validate(Context ctx);
}
