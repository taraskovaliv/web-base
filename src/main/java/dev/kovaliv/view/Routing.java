package dev.kovaliv.view;

import io.javalin.Javalin;

public interface Routing {

    void addEndpoints(Javalin app);
}
