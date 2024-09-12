package dev.kovaliv.services;

import io.javalin.http.Context;

public interface UserValidation {

    boolean isAuthenticated(Context ctx);

    boolean authenticate(Context ctx);
}
