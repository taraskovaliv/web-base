package dev.kovaliv.services;

import io.javalin.http.Context;

public class DefaultUserValidation implements UserValidation {

    @Override
    public boolean isAuthenticated(Context ctx) {
        return false;
    }

    @Override
    public boolean authenticate(Context ctx) {
        return false;
    }
}
