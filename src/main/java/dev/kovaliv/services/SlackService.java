package dev.kovaliv.services;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static dev.kovaliv.config.ContextConfig.SLACK_PROFILE;
import static dev.kovaliv.config.ContextConfig.env;

@Profile(SLACK_PROFILE)
@Service
public class SlackService {

    private final MethodsClient slackClient = Slack.getInstance().methods();

    @SneakyThrows
    public void send(String text, String channel) {
        slackClient.chatPostMessage(
                ChatPostMessageRequest.builder()
                        .token(env().getProperty("slack.token"))
                        .text(text)
                        .channel(channel)
                        .build()
        );
    }
}
