package dev.kovaliv.services;

import io.javalin.http.Context;

public interface UserValidation {
    boolean validate(Context ctx);
}
