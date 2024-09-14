package dev.kovaliv.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorUtils {
    private static ExecutorService executor;

    public static ExecutorService executor() {
        if (executor == null || executor.isShutdown() || executor.isTerminated()) {
            executor = Executors.newFixedThreadPool(4);
        }
        return executor;
    }
}
