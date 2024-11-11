package dev.kovaliv.view;

import com.google.gson.reflect.TypeToken;
import io.javalin.http.Context;
import j2html.tags.DomContent;
import j2html.tags.specialized.DivTag;
import j2html.tags.specialized.SectionTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.kovaliv.utils.GsonUtils.gson;
import static j2html.TagCreator.*;

public class Messages {

    public static void addMessage(Context ctx, MessageType type, String message) {
        Map<MessageType, List<String>> messages = parseMessages(ctx);
        messages.putIfAbsent(type, new ArrayList<>());
        messages.get(type).add(message);
        ctx.sessionAttribute("messages", gson().toJson(messages));
    }

    public static void addMessage(Context ctx, MessageType type, List<String> message) {
        Map<MessageType, List<String>> messages = parseMessages(ctx);
        messages.putIfAbsent(type, new ArrayList<>());
        messages.get(type).addAll(message);
        ctx.sessionAttribute("messages", gson().toJson(messages));
    }

    public static @NotNull DomContent[] getMessagesContent(Context ctx) {
        Map<MessageType, List<String>> messages = parseMessages(ctx);
        DomContent[] content = new DomContent[messages.isEmpty() ? 0 : 1];
        if (!messages.isEmpty()) {
            content[0] = messagesBlock(messages);
        }
        return content;
    }

    private static Map<MessageType, List<String>> parseMessages(Context ctx) {
        Map<MessageType, List<String>> messages = new HashMap<>();
        String messagesString = ctx.sessionAttribute("messages");
        if (messagesString != null) {
            Map<MessageType, List<String>> messagesTmp = gson().fromJson(messagesString, new TypeToken<Map<MessageType, List<String>>>() {
            }.getType());
            ctx.sessionAttribute("messages", null);
            for (Map.Entry<MessageType, List<String>> entry : messagesTmp.entrySet()) {
                messages.putIfAbsent(entry.getKey(), new ArrayList<>());
                messages.get(entry.getKey()).addAll(entry.getValue());
            }
        }
        return messages;
    }

    private static SectionTag messagesBlock(Map<MessageType, List<String>> messages) {
        List<DivTag> messagesDiv = new ArrayList<>();
        for (Map.Entry<MessageType, List<String>> entry : messages.entrySet()) {
            for (String message : entry.getValue()) {
                messagesDiv.add(div(
                        i().withClasses("fa", entry.getKey().getIcon()).attr("aria-hidden", "true"),
                        span(" " + message),
                        button()
                                .withType("button")
                                .withClass("btn-close")
                                .attr("data-bs-dismiss", "alert")
                                .attr("aria-label", "Close")
                ).withClasses("alert", entry.getKey().getCssClass(), "alert-dismissible"));
            }
        }
        return section(
                div(
                        div(
                                messagesDiv.toArray(DivTag[]::new)
                        ).withClass("row")
                ).withClasses("container")
        ).withClasses("small-section", "bg-dark", "light-content")
                .withStyle("padding: 100px 0 0 0");
    }

    public enum MessageType {
        ERROR, WARNING, INFO, SUCCESS;

        public String getCssClass() {
            return switch (this) {
                case ERROR -> "alert-danger";
                case WARNING -> "alert-warning";
                case INFO -> "alert-info";
                case SUCCESS -> "alert-success";
            };
        }

        public String getIcon() {
            return switch (this) {
                case ERROR -> "fa-times-circle";
                case WARNING -> "fa-exclamation-circle";
                case INFO -> "fa-info-circle";
                case SUCCESS -> "fa-check-circle";
            };
        }
    }
}
